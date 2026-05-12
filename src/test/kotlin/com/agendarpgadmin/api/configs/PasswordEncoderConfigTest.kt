package com.agendarpgadmin.api.configs

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

class PasswordEncoderConfigTest {

    @Test
    fun `deve instanciar Argon2PasswordEncoder configurado com o padrao v5_8`() {
        val config = PasswordEncoderConfig()
        val encoder = config.passwordEncoder()

        assertNotNull(encoder)
        assertTrue(encoder is Argon2PasswordEncoder)
        
        // Verifica se ele realmente gera no padrão Argon2id (testando o funcionamento do bean)
        val hash = encoder.encode("teste123")
        assertTrue(hash.startsWith("\$argon2id\$"))
    }
}