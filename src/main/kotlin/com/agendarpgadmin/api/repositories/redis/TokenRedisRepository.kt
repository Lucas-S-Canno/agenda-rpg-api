package com.agendarpgadmin.api.repositories.redis

import org.springframework.data.redis.core.RedisTemplate
import org.springframework.beans.factory.ObjectProvider
import org.springframework.stereotype.Repository

/**
 * Repositório Redis para gerenciar o estado dos Access Tokens (Whitelist/Sessions).
 */
@Repository
class TokenRedisRepository(
    redisTemplateProvider: ObjectProvider<RedisTemplate<String, Any>>
) : BaseRedisRepository<String>(redisTemplateProvider.getIfAvailable(), "auth:token")
