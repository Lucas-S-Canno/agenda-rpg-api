package com.agendarpgadmin.api.repositories

import com.agendarpgadmin.api.entities.RefreshTokenEntity
import com.agendarpgadmin.api.entities.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, UUID> {
    fun findByUser(user: UserEntity): List<RefreshTokenEntity>
    fun deleteByUser(user: UserEntity)
    fun deleteByUserEmail(email: String)
}
