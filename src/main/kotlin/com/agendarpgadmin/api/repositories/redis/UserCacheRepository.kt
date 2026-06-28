package com.agendarpgadmin.api.repositories.redis

import com.agendarpgadmin.api.entities.UserEntity
import org.springframework.beans.factory.ObjectProvider
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class UserCacheRepository(
    redisTemplateProvider: ObjectProvider<RedisTemplate<String, Any>>
) : BaseRedisRepository<UserEntity>(redisTemplateProvider.getIfAvailable(), "user")
