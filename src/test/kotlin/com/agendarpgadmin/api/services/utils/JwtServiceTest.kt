package com.agendarpgadmin.api.services.utils

import com.agendarpgadmin.api.entities.RefreshTokenEntity
import com.agendarpgadmin.api.entities.UserEntity
import com.agendarpgadmin.api.repositories.RefreshTokenRepository
import com.agendarpgadmin.api.repositories.redis.TokenRedisRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.*

/**
 * Testes de Unidade: Provando que nosso mestre dos segredos é incorruptível.
 */
class JwtServiceTest {

    private val tokenRedisRepository = mockk<TokenRedisRepository>(relaxed = true)
    private val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
    private val passwordHashingService = mockk<PasswordHashingService>()
    
    // Segredo de teste (deve ser longo o suficiente para HS256)
    private val secret = "meu_segredo_super_secreto_e_extremamente_longo_para_evitar_erros_de_assinatura"
    private val accessTokenExpiration = 3600000L
    private val refreshTokenExpirationDays = 7L

    private lateinit var jwtService: JwtService

    @BeforeEach
    fun setup() {
        jwtService = JwtService(
            secret = secret,
            accessTokenExpiration = accessTokenExpiration,
            refreshTokenExpirationDays = refreshTokenExpirationDays,
            tokenRedisRepository = tokenRedisRepository,
            refreshTokenRepository = refreshTokenRepository,
            passwordHashingService = passwordHashingService
        )
    }

    @Test
    fun `deve gerar um access token valido e salvar no redis`() {
        val user = UserEntity().apply {
            id = UUID.randomUUID()
            email = "mestre@taverna.com"
            tipo = "ADM"
            nomeCompleto = "Mestre do Bigode"
        }

        val token = jwtService.generateAccessToken(user)

        assertNotNull(token)
        // Verifica se persistiu no Redis com o TTL correto
        verify { tokenRedisRepository.save(token, user.email!!, accessTokenExpiration / 1000) }
    }

    @Test
    fun `deve validar um access token corretamente se estiver na whitelist`() {
        val user = UserEntity().apply {
            id = UUID.randomUUID()
            email = "player@dungeon.com"
            tipo = "JGD"
        }
        val token = jwtService.generateAccessToken(user)

        every { tokenRedisRepository.exists(token) } returns true

        val claims = jwtService.validateAccessToken(token)
        
        assertNotNull(claims)
        assertEquals(user.email, claims?.subject)
        assertEquals(user.id.toString(), claims?.get("id"))
    }

    @Test
    fun `deve invalidar token se nao estiver no redis (revogado)`() {
        val user = UserEntity().apply {
            id = UUID.randomUUID()
            email = "ladrao@guilda.com"
        }
        val token = jwtService.generateAccessToken(user)

        every { tokenRedisRepository.exists(token) } returns false

        val claims = jwtService.validateAccessToken(token)
        assertNull(claims)
    }

    @Test
    fun `deve gerar refresh token composto (id ponto segredo)`() {
        val user = UserEntity().apply { id = UUID.randomUUID() }
        val tokenId = UUID.randomUUID()
        
        every { passwordHashingService.hashPassword(any()) } returns "hash_secreto"
        every { refreshTokenRepository.save(any<RefreshTokenEntity>()) } answers { 
            val entity = firstArg<RefreshTokenEntity>()
            entity.id = tokenId
            entity
        }

        val compositeToken = jwtService.generateRefreshToken(user)

        assertNotNull(compositeToken)
        assertTrue(compositeToken.startsWith(tokenId.toString()))
        assertTrue(compositeToken.contains("."))
        
        val secretPart = compositeToken.split(".")[1]
        verify { passwordHashingService.hashPassword(secretPart) }
    }

    @Test
    fun `deve renovar tokens com sucesso usando refresh token valido`() {
        val user = UserEntity().apply { 
            id = UUID.randomUUID()
            email = "mago@torre.com"
            nomeCompleto = "Mago Cinzento"
            tipo = "COORD"
        }
        val tokenId = UUID.randomUUID()
        val rawSecret = "segredo_magico"
        val composite = "$tokenId.$rawSecret"
        
        val entity = RefreshTokenEntity(
            id = tokenId,
            user = user,
            tokenHash = "hash_do_segredo",
            expiresAt = Instant.now().plusSeconds(3600)
        )

        every { refreshTokenRepository.findById(tokenId) } returns Optional.of(entity)
        every { passwordHashingService.verifyPassword(rawSecret, "hash_do_segredo") } returns true
        every { passwordHashingService.hashPassword(any()) } returns "novo_hash"
        every { refreshTokenRepository.save(any()) } answers { 
            val e = firstArg<RefreshTokenEntity>()
            e.id = UUID.randomUUID()
            e
        }

        val result = jwtService.refreshTokens(composite)

        assertNotNull(result)
        assertNotNull(result?.accessToken)
        assertNotNull(result?.refreshToken)
        
        // Verifica se rotacionou (deletou o antigo e criou novo)
        verify { refreshTokenRepository.delete(entity) }
        verify { refreshTokenRepository.save(any()) }
    }

    @Test
    fun `deve revogar todos os tokens se houver tentativa de reuso de refresh token revogado`() {
        val user = UserEntity().apply { id = UUID.randomUUID() }
        val tokenId = UUID.randomUUID()
        val composite = "$tokenId.segredo"
        
        val entity = RefreshTokenEntity(
            id = tokenId,
            user = user,
            revoked = true, // Token já marcado como revogado!
            expiresAt = Instant.now().plusSeconds(3600)
        )

        every { refreshTokenRepository.findById(tokenId) } returns Optional.of(entity)

        val result = jwtService.refreshTokens(composite)

        assertNull(result)
        // Medida drástica: Deleta todos os refresh tokens do usuário
        verify { refreshTokenRepository.deleteByUser(user) }
    }
}
