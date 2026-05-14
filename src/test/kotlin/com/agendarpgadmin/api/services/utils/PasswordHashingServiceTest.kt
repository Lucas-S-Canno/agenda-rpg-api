package com.agendarpgadmin.api.services.utils

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder

class PasswordHashingServiceTest {

    private lateinit var passwordHashingService: PasswordHashingService
    private lateinit var passwordEncoder: Argon2PasswordEncoder

    @BeforeEach
    fun setUp() {
        // Usamos a mesma configuração que o Bean do Spring Security
        passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8() as Argon2PasswordEncoder
        passwordHashingService = PasswordHashingService(passwordEncoder)
    }

    @Test
    fun `deve gerar um hash com a estrutura do Argon2id`() {
        val rawPassword = "minhaSenhaSuperSecreta123"
        val hashedPassword = passwordHashingService.hashPassword(rawPassword)

        assertNotNull(hashedPassword)
        // Verifica se gerou usando Argon2id (o prefixo padrão)
        assertTrue(hashedPassword.startsWith("\$argon2id\$"))
    }

    @Test
    fun `deve validar corretamente uma senha que corresponde ao hash`() {
        val rawPassword = "password123"
        val hashedPassword = passwordHashingService.hashPassword(rawPassword)

        val isValid = passwordHashingService.verifyPassword(rawPassword, hashedPassword)

        assertTrue(isValid)
    }

    @Test
    fun `deve falhar ao validar uma senha incorreta`() {
        val rawPassword = "password123"
        val wrongPassword = "Password123" // Note a letra maiúscula
        val hashedPassword = passwordHashingService.hashPassword(rawPassword)

        val isValid = passwordHashingService.verifyPassword(wrongPassword, hashedPassword)

        assertFalse(isValid)
    }

    @Test
    fun `deve falhar ao validar quando a senha ou o hash sao nulos ou vazios`() {
        assertFalse(passwordHashingService.verifyPassword(null, "hashQualquer"))
        assertFalse(passwordHashingService.verifyPassword("senhaQualquer", null))
        assertFalse(passwordHashingService.verifyPassword("", "hashQualquer"))
        assertFalse(passwordHashingService.verifyPassword("senhaQualquer", ""))
    }

    @Test
    fun `deve lancar IllegalArgumentException ao tentar fazer hash de senha nula ou vazia`() {
        assertThrows<IllegalArgumentException> {
            passwordHashingService.hashPassword(null)
        }
        
        assertThrows<IllegalArgumentException> {
            passwordHashingService.hashPassword("")
        }

        assertThrows<IllegalArgumentException> {
            passwordHashingService.hashPassword("   ") // Só espaços
        }
    }

    @Test
    fun `hashes da mesma senha devem ser diferentes devido ao salt aleatorio`() {
        val rawPassword = "password123"
        
        val hash1 = passwordHashingService.hashPassword(rawPassword)
        val hash2 = passwordHashingService.hashPassword(rawPassword)

        assertNotEquals(hash1, hash2)
        
        // Mas ambos devem ser válidos para a mesma senha
        assertTrue(passwordHashingService.verifyPassword(rawPassword, hash1))
        assertTrue(passwordHashingService.verifyPassword(rawPassword, hash2))
    }
}
