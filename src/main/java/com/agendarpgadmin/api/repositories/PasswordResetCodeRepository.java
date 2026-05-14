package com.agendarpgadmin.api.repositories;
import java.util.UUID;

import com.agendarpgadmin.api.entities.PasswordResetCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository extends JpaRepository<PasswordResetCodeEntity, UUID> {
    Optional<PasswordResetCodeEntity> findByEmailAndCodeAndUsedFalse(String email, String code);
    Optional<PasswordResetCodeEntity> findByEmailAndResetTokenAndUsedFalse(String email, String resetToken);
}
