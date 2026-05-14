package com.agendarpgadmin.api.services.utils

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

/**
 * Serviço responsável pelo hashing e verificação de senhas usando o algoritmo Argon2id.
 */
@Service
class PasswordHashingService(private val passwordEncoder: PasswordEncoder) {

    /**
     * Gera o hash seguro de uma senha em texto plano.
     *
     * @param rawPassword A senha em texto plano.
     * @return O hash gerado (Argon2id).
     */
    fun hashPassword(rawPassword: String?): String {
        require(!rawPassword.isNullOrBlank()) { "A senha não pode estar vazia para gerar o hash." }
        return passwordEncoder.encode(rawPassword)
    }

    /**
     * Verifica se uma senha em texto plano corresponde a um hash existente.
     *
     * @param rawPassword A senha em texto plano fornecida pelo usuário.
     * @param encodedPassword O hash da senha salvo no banco de dados.
     * @return `true` se a senha corresponder ao hash, `false` caso contrário.
     */
    fun verifyPassword(rawPassword: String?, encodedPassword: String?): Boolean {
        if (rawPassword.isNullOrEmpty() || encodedPassword.isNullOrEmpty()) {
            return false
        }
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
}