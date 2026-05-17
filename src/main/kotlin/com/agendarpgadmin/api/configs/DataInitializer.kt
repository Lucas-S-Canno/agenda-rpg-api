package com.agendarpgadmin.api.configs

import com.agendarpgadmin.api.repositories.UserRepository
import com.agendarpgadmin.api.services.utils.PasswordHashingService
import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DataInitializer(
    private val userRepository: UserRepository,
    private val passwordHashingService: PasswordHashingService
) : ApplicationRunner {
    
    private val logger = LoggerFactory.getLogger(DataInitializer::class.java)

    @Transactional
    override fun run(args: ApplicationArguments?) {
        try {
            // Procura por usuários com senha inválida (malformadas/corrupted)
            val adminUser = userRepository.findByEmail("admin@email.com").orElse(null)
            
            if (adminUser != null) {
                val storedPassword = adminUser.getPassword()
                var corrected = false
                
                // Verifica se o hash está malformado (não começa com $argon2id$)
                if (!storedPassword.startsWith("\$argon2id\$")) {
                    logger.warn("Detectado hash de senha malformado para admin@email.com. Corrigindo...")
                    
                    // Regenera o hash com a senha conhecida
                    val newHash = passwordHashingService.hashPassword("Senha@123")
                    userRepository.updatePasswordByEmail("admin@email.com", newHash)
                    corrected = true
                }
                
                // Verifica se o email_verified está true
                if (adminUser.getEmailVerified() != true) {
                    logger.warn("Email não verificado para admin@email.com. Marcando como verificado...")
                    userRepository.markEmailAsVerified("admin@email.com")
                    corrected = true
                }
                
                if (corrected) {
                    logger.info("Conta admin@email.com foi atualizada com sucesso.")
                }
            }
        } catch (e: Exception) {
            logger.error("Erro ao tentar corrigir dados durante inicialização", e)
            // Não falha a aplicação por isso
        }
    }
}








