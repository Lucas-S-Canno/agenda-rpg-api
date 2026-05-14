package com.agendarpgadmin.api.entities;

import com.agendarpgadmin.api.services.utils.UuidUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "email_verification_tokens", indexes = {
    @Index(name = "ux_email_verif_token", columnList = "token", unique = true),
    @Index(name = "ix_email_verif_user", columnList = "userId")
})
@Getter
@Setter
@NoArgsConstructor
public class EmailVerificationToken {
    @Id
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UuidUtils.generateV7();
        }
    }

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(nullable = false, unique = true, length = 200)
    private String token;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "consumed_at")
    private LocalDateTime consumedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "resend_count")
    private Integer resendCount = 0;

    @Column(name = "resend_available_at")
    private LocalDateTime resendAvailableAt;
}
