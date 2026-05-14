package com.agendarpgadmin.api.services

import com.agendarpgadmin.api.entities.UserEntity
import com.agendarpgadmin.api.repositories.redis.UserCacheRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class UserCacheService(
    private val userCacheRepository: UserCacheRepository
) {
    private val logger = LoggerFactory.getLogger(UserCacheService::class.java)

    /**
     * Cacheia um usuário por 1 hora.
     */
    fun cacheUser(user: UserEntity) {
        try {
            userCacheRepository.save(user.getId().toString(), user, 3600)
            logger.info("Usuário ${user.getId()} cacheado com sucesso.")
        } catch (e: Exception) {
            logger.error("Erro ao cachear usuário ${user.getId()}: ${e.message}")
        }
    }

    /**
     * Busca um usuário no cache.
     */
    fun getCachedUser(userId: String): UserEntity? {
        return try {
            userCacheRepository.find(userId)
        } catch (e: Exception) {
            logger.error("Erro ao buscar usuário $userId no cache: ${e.message}")
            null
        }
    }

    /**
     * Remove o usuário do cache (útil após atualizações).
     */
    fun evictUser(userId: String) {
        try {
            userCacheRepository.delete(userId)
            logger.info("Usuário $userId removido do cache.")
        } catch (e: Exception) {
            logger.error("Erro ao remover usuário $userId do cache: ${e.message}")
        }
    }
}
