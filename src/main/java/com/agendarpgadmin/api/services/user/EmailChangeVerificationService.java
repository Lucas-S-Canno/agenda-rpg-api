package com.agendarpgadmin.api.services.user;

import com.agendarpgadmin.api.entities.EmailChangeChallengeEntity;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.EmailChangeChallengeRepository;
import com.agendarpgadmin.api.repositories.UserRepository;
import com.agendarpgadmin.api.services.UserCacheService;
import com.agendarpgadmin.api.services.utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
public class EmailChangeVerificationService {

    @Autowired
    private EmailChangeChallengeRepository challengeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserCacheService userCacheService;

    public void requestCode(String authenticatedEmail) {
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Se já existe um desafio ativo, bloqueia novo pedido
        challengeRepository.findByUserIdAndUsedAtIsNull(user.getId()).ifPresent(existing -> {
            if (existing.getExpiresAt() != null && existing.getExpiresAt().isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("Já existe um código ativo para esta solicitação");
            }
        });

        // Remove desafios antigos não usados
        challengeRepository.findByUserIdAndUsedAtIsNull(user.getId())
                .ifPresent(challengeRepository::delete);

        EmailChangeChallengeEntity challenge = new EmailChangeChallengeEntity();
        challenge.setUserId(user.getId());
        challenge.setCode(generateCode());
        challenge.setVerificationToken(generateToken());
        challenge.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        challenge.setAttempts(0);
        challengeRepository.save(challenge);

        emailService.sendEmailChangeCode(user.getEmail(), challenge.getCode());
    }

    @Transactional
    public String validateCode(String authenticatedEmail, String code) {
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        String normalizedCode = code == null ? "" : code.trim().toUpperCase();
        EmailChangeChallengeEntity challenge = challengeRepository
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
    public void confirmEmailChange(String authenticatedEmail, String novoEmail, String tokenVerificacao) {
        UserEntity user = userRepository.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (novoEmail == null || novoEmail.trim().isEmpty()) {
            throw new IllegalArgumentException("Novo e-mail não pode ser vazio");
        }

        String novoEmailNormalizado = novoEmail.trim().toLowerCase();

        if (novoEmailNormalizado.equals(user.getEmail())) {
            throw new IllegalArgumentException("O novo e-mail não pode ser igual ao e-mail atual");
        }

        if (userRepository.findByEmail(novoEmailNormalizado).isPresent()) {
            throw new IllegalArgumentException("Este e-mail já está em uso por outro usuário");
        }

        if (tokenVerificacao == null || tokenVerificacao.trim().isEmpty()) {
            throw new IllegalArgumentException("Token de validação não informado");
        }

        EmailChangeChallengeEntity challenge = challengeRepository
                .findByUserIdAndVerificationTokenAndUsedAtIsNull(user.getId(), tokenVerificacao)
                .orElseThrow(() -> new IllegalArgumentException("Token de validação inválido"));

        if (challenge.getValidatedAt() == null) {
            throw new IllegalStateException("O código ainda não foi validado");
        }

        if (challenge.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token de validação expirado");
        }

        user.setEmail(novoEmailNormalizado);
        userRepository.save(user);

        challenge.setUsedAt(LocalDateTime.now());
        challengeRepository.save(challenge);

        userCacheService.evictUser(user.getId().toString());
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

