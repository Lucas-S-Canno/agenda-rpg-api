package com.agendarpgadmin.api.services.utils

import com.agendarpgadmin.api.dtos.LoginResponseDTO
import com.agendarpgadmin.api.entities.RefreshTokenEntity
import com.agendarpgadmin.api.entities.UserEntity
import com.agendarpgadmin.api.repositories.RefreshTokenRepository
import com.agendarpgadmin.api.repositories.redis.TokenRedisRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

/**
 * Maestro da Segurança: Gerencia geração, validação e persistência de tokens (Access & Refresh).
 */
@Service
class JwtService(
    @param:Value("\${jwt.secret}") private val secret: String,
    @param:Value("\${jwt.access-token.expiration:86400000}") private val accessTokenExpiration: Long,
    @param:Value("\${jwt.refresh-token.expiration-days:7}") private val refreshTokenExpirationDays: Long,
    private val tokenRedisRepository: TokenRedisRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordHashingService: PasswordHashingService
) {
    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    /**
     * Gera um Access Token e salva no Redis (Whitelist).
     */
    fun generateAccessToken(user: UserEntity): String {
        val now = Date()
        val validity = Date(now.time + accessTokenExpiration)

        val token = Jwts.builder()
            .setSubject(user.getEmail())
            .claim("nomeCompleto", user.getNomeCompleto())
            .claim("tipo", user.getTipo())
            .claim("id", user.getId().toString())
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()

        // Whitelist no Redis para controle de sessão/logout (best-effort, pode falhar se Redis não estiver disponível)
        try {
            tokenRedisRepository.save(token, user.getEmail() ?: "unknown", accessTokenExpiration / 1000)
        } catch (e: Exception) {
            // Redis não disponível, continua com token válido (sem whitelist temporariamente)
            // Log via SLF4J se necessário, mas não afeta o fluxo de autenticação
        }

        return token
    }

    /**
     * Gera um Refresh Token, salva o hash no banco e retorna o token plano (id.secret).
     */
    fun generateRefreshToken(user: UserEntity): String {
        val secret = UUID.randomUUID().toString()
        val expiryDate = Instant.now().plus(refreshTokenExpirationDays, ChronoUnit.DAYS)

        val hashedToken = passwordHashingService.hashPassword(secret)

        val refreshTokenEntity = RefreshTokenEntity(
            user = user,
            tokenHash = hashedToken,
            expiresAt = expiryDate
        )

        val saved = refreshTokenRepository.save(refreshTokenEntity)

        // Retornamos ID.SECRET para busca eficiente no refresh
        return "${saved.id}.${secret}"
    }

    /**
     * Valida um Refresh Token composite (id.secret) e gera um novo par de tokens.
     * Implementa rotação de Refresh Token para maior segurança.
     */
    fun refreshTokens(compositeToken: String): LoginResponseDTO? {
        val parts = compositeToken.split(".")
        if (parts.size != 2) return null

        val tokenId = try { UUID.fromString(parts[0]) } catch (_: Exception) { return null }
        val secret = parts[1]

        val tokenEntity = refreshTokenRepository.findById(tokenId).orElse(null) ?: return null

        // Verifica validade
        if (tokenEntity.revoked || tokenEntity.expiresAt?.isBefore(Instant.now()) == true) {
            // Se o token foi revogado mas alguém tentou usar, podemos revogar TODOS do usuário por segurança
            // (Detecção de reúso de refresh token - opcional mas recomendado)
            refreshTokenRepository.deleteByUser(tokenEntity.user!!)
            return null
        }

        // Verifica o segredo contra o hash
        if (!passwordHashingService.verifyPassword(secret, tokenEntity.tokenHash)) {
            return null
        }

        val user = tokenEntity.user ?: return null

        // Rotação: Deleta o antigo
        refreshTokenRepository.delete(tokenEntity)

        // Gera novos
        val newAccessToken = generateAccessToken(user)
        val newRefreshToken = generateRefreshToken(user)

        return LoginResponseDTO(newAccessToken, newRefreshToken)
    }

    /**
     * Valida um Access Token contra a assinatura e a whitelist do Redis.
     */
    fun validateAccessToken(token: String): Claims? {
        return try {
            val claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .body

            // Verifica se o token ainda é bem-vindo na nossa taverna (Redis)
            // Se Redis não estiver disponível, permite o token (best-effort)
            try {
                if (!tokenRedisRepository.exists(token)) {
                    return null
                }
            } catch (e: Exception) {
                // Redis não disponível, permite o token (sem revogação temporária)
            }

            claims
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Revoga um Access Token (Logout).
     */
    fun revokeAccessToken(token: String) {
        try {
            tokenRedisRepository.delete(token)
        } catch (e: Exception) {
            // Redis não disponível, token continua válido até expirar naturalmente
        }
    }

    /**
     * Revoga TODOS os tokens de um usuário (Access e Refresh).
     * Útil para logout global ou quando uma conta é comprometida.
     */
    fun revokeAllUserTokens(email: String) {
        // 1. Remove refresh tokens do banco
        refreshTokenRepository.deleteByUserEmail(email)
        // 2. Nota: Access tokens no Redis expiram sozinhos, 
        // mas o ideal seria ter uma lista por usuário se quiséssemos revogar todos os access tokens ativos.
        // Por enquanto, o principal é o refresh token.
    }

    /**
     * Utilitários para extração de claims
     */
    fun getEmailFromToken(token: String): String? {
        return validateAccessToken(token)?.subject
    }

    fun getUserIdFromToken(token: String): String? {
        val claims = validateAccessToken(token) ?: return null
        val userId = claims["id"]

        return when (userId) {
            is Number -> userId.toLong().toString()
            is String -> userId.takeIf { it.isNotBlank() }
            else -> null
        }
    }

    fun getUserTypeFromToken(token: String): String? {
        return validateAccessToken(token)?.get("tipo", String::class.java)
    }

    /**
     * Lê o tipo do usuário diretamente do token JWT, sem validar contra Redis.
     * Útil para validações rápidas onde a whitelist não é crítica.
     */
    fun getUserTypeDirectFromToken(token: String): String? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .body
                .get("tipo", String::class.java)
        } catch (_: Exception) {
            null
        }
    }

    /**
     * Extrai email direto do token JWT, sem validar contra Redis.
     * Útil para validações onde a whitelist não é crítica.
     */
    fun getEmailDirectFromToken(token: String): String? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .body
                .subject
        } catch (_: Exception) {
            null
        }
    }

    fun isUserOneOfTypes(token: String, allowedTypes: Set<String>): Boolean {
        val userType = getUserTypeFromToken(token)
        return userType != null && allowedTypes.contains(userType)
    }
}
