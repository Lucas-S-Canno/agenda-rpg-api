package com.agendarpgadmin.api.services

import com.agendarpgadmin.api.entities.UserEntity
import com.agendarpgadmin.api.repositories.redis.UserCacheRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyLong
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.util.*

class UserCacheServiceTest {

    @Mock
    private lateinit var userCacheRepository: UserCacheRepository

    private lateinit var userCacheService: UserCacheService

    private fun <T> anyObj(): T {
        any<T>()
        @Suppress("UNCHECKED_CAST")
        return null as T
    }

    private fun <T> eqObj(value: T): T {
        eq(value)
        return value
    }

    @BeforeEach
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userCacheService = UserCacheService(userCacheRepository)
    }

    @Test
    fun `should cache user successfully`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserEntity().apply {
            setId(userId)
            setEmail("test@example.com")
        }

        // When
        userCacheService.cacheUser(user)

        // Then
        verify(userCacheRepository, times(1)).save(eqObj(userId.toString()), eqObj(user), anyLong())
    }

    @Test
    fun `should return cached user when found`() {
        // Given
        val userId = UUID.randomUUID()
        val user = UserEntity().apply {
            setId(userId)
            setEmail("test@example.com")
        }
        `when`(userCacheRepository.find(userId.toString())).thenReturn(user)

        // When
        val result = userCacheService.getCachedUser(userId.toString())

        // Then
        assertNotNull(result)
        assertEquals(userId, result?.getId())
        assertEquals("test@example.com", result?.getEmail())
        verify(userCacheRepository, times(1)).find(userId.toString())
    }

    @Test
    fun `should return null when user not in cache`() {
        // Given
        val userId = UUID.randomUUID().toString()
        `when`(userCacheRepository.find(userId)).thenReturn(null)

        // When
        val result = userCacheService.getCachedUser(userId)

        // Then
        assertNull(result)
        verify(userCacheRepository, times(1)).find(userId)
    }

    @Test
    fun `should evict user from cache`() {
        // Given
        val userId = UUID.randomUUID().toString()

        // When
        userCacheService.evictUser(userId)

        // Then
        verify(userCacheRepository, times(1)).delete(userId)
    }

    @Test
    fun `should handle exception during caching`() {
        // Given
        val user = UserEntity().apply {
            setId(UUID.randomUUID())
        }
        doThrow(RuntimeException("Redis is down")).`when`(userCacheRepository).save(anyObj(), anyObj(), anyLong())

        // When & Then (Should not throw exception, just log it)
        assertDoesNotThrow {
            userCacheService.cacheUser(user)
        }
    }
}
