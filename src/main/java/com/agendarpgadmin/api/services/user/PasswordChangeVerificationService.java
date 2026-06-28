package com.agendarpgadmin.api.services.user;

import com.agendarpgadmin.api.entities.PasswordChangeChallengeEntity;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.PasswordChangeChallengeRepository;
import com.agendarpgadmin.api.repositories.UserRepository;
import com.agendarpgadmin.api.services.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class PasswordChangeVerificationService {

    @Autowired
    private PasswordChangeChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    public void requestCode(String authenticatedEmail) {
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        challengeRepository.findByUserIdAndUsedAtIsNull(user.getId()).ifPresent(existing -> {
            if (existing.getExpiresAt() != null && existing.getExpiresAt().isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("Já existe um código ativo para esta solicitação");
            }
        });

        challengeRepository.findByUserIdAndUsedAtIsNull(user.getId()).ifPresent(challengeRepository::delete);

        PasswordChangeChallengeEntity challenge = new PasswordChangeChallengeEntity();
        challenge.setUserId(user.getId());
        challenge.setCode(generateCode());
        challenge.setVerificationToken(generateToken());
        challenge.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        challenge.setAttempts(0);
        challengeRepository.save(challenge);

        emailService.sendPasswordChangeCode(user.getEmail(), challenge.getCode());
    }

    @Transactional
    public String validateCode(String authenticatedEmail, String code) {
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String normalizedCode = code == null ? "" : code.trim().toUpperCase();
        PasswordChangeChallengeEntity challenge = challengeRepository
                .findByUserIdAndCodeAndUsedAtIsNull(user.getId(), normalizedCode)
                .orElseThrow(() -> new IllegalArgumentException("Código inválido ou expirado"));

        if (challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Código inválido ou expirado");
        }

        challenge.setValidatedAt(LocalDateTime.now());
        challenge.setAttempts((challenge.getAttempts() == null ? 0 : challenge.getAttempts()) + 1);
        challengeRepository.save(challenge);
        return challenge.getVerificationToken();
    }

    @Transactional
    public void consumeValidatedToken(String authenticatedEmail, String token) {
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        PasswordChangeChallengeEntity challenge = challengeRepository
                .findByUserIdAndVerificationTokenAndUsedAtIsNull(user.getId(), token)
                .orElseThrow(() -> new IllegalArgumentException("Token de validação inválido"));

        if (challenge.getValidatedAt() == null) {
            throw new IllegalStateException("O código ainda não foi validado");
        }

        if (challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token de validação expirado");
        }

        challenge.setUsedAt(LocalDateTime.now());
        challengeRepository.save(challenge);
    }

    public void assertValidatedToken(String authenticatedEmail, String token) {
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        PasswordChangeChallengeEntity challenge = challengeRepository
                .findByUserIdAndVerificationTokenAndUsedAtIsNull(user.getId(), token)
                .orElseThrow(() -> new IllegalArgumentException("Token de validação inválido"));

        if (challenge.getValidatedAt() == null) {
            throw new IllegalStateException("O código ainda não foi validado");
        }

        if (challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token de validação expirado");
        }
    }

    private String generateCode() {
        int code = new SecureRandom().nextInt(900000) + 100000;
        return String.valueOf(code);
    }

    private String generateToken() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}

