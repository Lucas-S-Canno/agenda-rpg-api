package com.agendarpgadmin.api.repositories.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
abstract class BaseRedisRepository<T : Any>(
    protected val redisTemplate: RedisTemplate<String, Any>,
    private val prefix: String
) {
    /**
     * Salva um valor no cache com um tempo de expiração.
     */
    fun save(key: String, value: T, ttlSeconds: Long) {
        redisTemplate.opsForValue().set(buildKey(key), value, ttlSeconds, TimeUnit.SECONDS)
    }

    /**
     * Busca um valor no cache.
     */
    @Suppress("UNCHECKED_CAST")
    fun find(key: String): T? {
        return redisTemplate.opsForValue().get(buildKey(key)) as? T
    }

    /**
     * Remove um valor do cache.
     */
    fun delete(key: String) {
        redisTemplate.delete(buildKey(key))
    }

    /**
     * Verifica se uma chave existe.
     */
    fun exists(key: String): Boolean {
        return redisTemplate.hasKey(buildKey(key))
    }

    private fun buildKey(key: String): String = "$prefix:$key"
}
