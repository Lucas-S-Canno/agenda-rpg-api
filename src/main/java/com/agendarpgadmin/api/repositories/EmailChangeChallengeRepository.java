package com.agendarpgadmin.api.repositories;

import com.agendarpgadmin.api.entities.EmailChangeChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EmailChangeChallengeRepository extends JpaRepository<EmailChangeChallengeEntity, UUID> {
    Optional<EmailChangeChallengeEntity> findByUserIdAndCodeAndUsedAtIsNull(UUID userId, String code);
    Optional<EmailChangeChallengeEntity> findByUserIdAndVerificationTokenAndUsedAtIsNull(UUID userId, String verificationToken);
    Optional<EmailChangeChallengeEntity> findByUserIdAndUsedAtIsNull(UUID userId);
    int deleteByExpiresAtBefore(LocalDateTime now);
}

