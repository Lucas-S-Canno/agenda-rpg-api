package com.agendarpgadmin.api.services.admin

import com.agendarpgadmin.api.dtos.LoginResponseDTO
import com.agendarpgadmin.api.repositories.UserRepository
import com.agendarpgadmin.api.services.utils.JwtService
import com.agendarpgadmin.api.services.utils.PasswordHashingService
import org.springframework.stereotype.Service

/**
 * Serviço de Autenticação: O Guardião dos Portões da Agenda.
 * Gerencia o fluxo de login e emissão de tokens.
 */
@Service
class LoginService(
    private val userRepository: UserRepository,
    private val passwordHashingService: PasswordHashingService,
    private val jwtService: JwtService
) {

    /**
     * Autentica um usuário e gera o par de tokens (Access & Refresh).
     * 
     * @param email Email do aventureiro
     * @param password Senha em texto plano
     * @return DTO com tokens ou null se as credenciais forem heresia
     * @throws IllegalStateException Se o email ainda não foi purificado (verificado)
     */
    fun authenticateUser(email: String, password: String): LoginResponseDTO? {
        val user = userRepository.findByEmail(email).orElse(null) ?: return null

        if (passwordHashingService.verifyPassword(password, user.getPassword())) {
            // Um cavaleiro sem honra não entra na taverna
            if (user.getEmailVerified() != true) {
                throw IllegalStateException("Email não verificado")
            }

            val accessToken = jwtService.generateAccessToken(user)
            val refreshToken = jwtService.generateRefreshToken(user)

            return LoginResponseDTO(accessToken, refreshToken)
        }
        
        return null
    }
}
