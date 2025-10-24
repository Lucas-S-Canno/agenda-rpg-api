package com.agendarpgadmin.api.repositories;

import com.agendarpgadmin.api.entities.EmailVerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmailVerificationTokenRepository extends JpaRepository<EmailVerificationToken, Long> {

    Optional<EmailVerificationToken> findByToken(String token);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken e WHERE e.userId = :userId AND e.consumedAt IS NULL")
    int deleteUnconsumedByUserId(@Param("userId") Long userId);

    @Modifying
    @Query("DELETE FROM EmailVerificationToken e WHERE e.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    @Query("SELECT COUNT(e) FROM EmailVerificationToken e WHERE e.userId = :userId AND e.createdAt >= :startOfDay")
    int countTodayResendsByUserId(@Param("userId") Long userId, @Param("startOfDay") LocalDateTime startOfDay);

    Optional<EmailVerificationToken> findByUserIdAndConsumedAtIsNull(Long userId);
}
