package com.agendarpgadmin.api.entities;

import com.agendarpgadmin.api.services.utils.UuidUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_change_challenges")
@Getter
@Setter
@NoArgsConstructor
public class PasswordChangeChallengeEntity {

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

	@Column(nullable = false, length = 20)
	private String code;

	@Column(name = "verification_token", nullable = false, unique = true, length = 200)
	private String verificationToken;

	@Column(name = "expires_at", nullable = false)
	private LocalDateTime expiresAt;

	@Column(name = "validated_at")
	private LocalDateTime validatedAt;

	@Column(name = "used_at")
	private LocalDateTime usedAt;

	@Column(name = "attempts", nullable = false)
	private Integer attempts = 0;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt = LocalDateTime.now();
}

