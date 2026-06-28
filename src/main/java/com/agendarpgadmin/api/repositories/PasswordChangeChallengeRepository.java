package com.agendarpgadmin.api.repositories;

import com.agendarpgadmin.api.entities.PasswordChangeChallengeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface PasswordChangeChallengeRepository extends JpaRepository<PasswordChangeChallengeEntity, UUID> {
    Optional<PasswordChangeChallengeEntity> findByUserIdAndCodeAndUsedAtIsNull(UUID userId, String code);
    Optional<PasswordChangeChallengeEntity> findByUserIdAndVerificationTokenAndUsedAtIsNull(UUID userId, String verificationToken);
    Optional<PasswordChangeChallengeEntity> findByUserIdAndUsedAtIsNull(UUID userId);
    int deleteByExpiresAtBefore(LocalDateTime now);
}
