package com.agendarpgadmin.api.repositories.redis

import com.agendarpgadmin.api.entities.UserEntity
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class UserCacheRepository(
    redisTemplate: RedisTemplate<String, Any>
) : BaseRedisRepository<UserEntity>(redisTemplate, "user")
