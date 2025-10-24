package com.agendarpgadmin.api.services.Public;

import com.agendarpgadmin.api.entities.EmailVerificationToken;
import com.agendarpgadmin.api.entities.UserEntity;
import com.agendarpgadmin.api.repositories.EmailVerificationTokenRepository;
import com.agendarpgadmin.api.repositories.UserRepository;
import com.agendarpgadmin.api.services.Utils.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;

@Service
public class EmailVerificationService {

    @Autowired
    private EmailVerificationTokenRepository tokenRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.email-verification.ttl-minutes:60}")
    private int ttlMinutes;

    @Value("${app.email-verification.resend-cooldown-minutes:5}")
    private int resendCooldownMinutes;

    @Value("${app.email-verification.daily-resend-limit:5}")
    private int dailyResendLimit;

    @Value("${app.frontend.base-url:http://localhost:3000}")
    private String frontendBaseUrl;

    @Transactional
    public void createAndSendVerificationLink(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        // Invalidar tokens anteriores não consumidos
        tokenRepository.deleteUnconsumedByUserId(userId);

        // Gerar token seguro
        String token = generateSecureToken();

        // Criar novo token
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUserId(userId);
        verificationToken.setToken(token);
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
        tokenRepository.save(verificationToken);

        // Montar link de verificação
        String verificationLink = frontendBaseUrl + "/verify-email?token=" + token;

        // Enviar email
        emailService.sendEmailVerification(user.getEmail(), user.getNomeCompleto(), verificationLink);
    }

    @Transactional
    public void verifyByToken(String token) {
        EmailVerificationToken verificationToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        // Verificar se já foi consumido (idempotência)
        if (verificationToken.getConsumedAt() != null) {
            return; // Já verificado
        }

        // Verificar expiração
        if (LocalDateTime.now().isAfter(verificationToken.getExpiresAt())) {
            throw new IllegalStateException("Token expirado");
        }

        // Buscar usuário
        UserEntity user = userRepository.findById(verificationToken.getUserId())
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado"));

        // Marcar email como verificado
        user.setEmailVerified(true);
        userRepository.save(user);

        // Marcar token como consumido
        verificationToken.setConsumedAt(LocalDateTime.now());
        tokenRepository.save(verificationToken);
    }

    @Transactional
    public void resendVerificationLink(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Email não encontrado"));

        // Verificar se o usuário já está verificado
        if (Boolean.TRUE.equals(user.getEmailVerified())) {
            throw new IllegalStateException("Email já verificado");
        }

        // Verificar cooldown
        Optional<EmailVerificationToken> activeToken = tokenRepository.findByUserIdAndConsumedAtIsNull(user.getId());
        if (activeToken.isPresent() && activeToken.get().getResendAvailableAt() != null) {
            if (LocalDateTime.now().isBefore(activeToken.get().getResendAvailableAt())) {
                throw new IllegalStateException("Aguarde antes de solicitar um novo código");
            }
        }

        // Verificar limite diário
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        int todayResends = tokenRepository.countTodayResendsByUserId(user.getId(), startOfDay);
        if (todayResends >= dailyResendLimit) {
            throw new IllegalStateException("Limite diário de reenvios excedido");
        }

        // Invalidar tokens anteriores e criar novo
        tokenRepository.deleteUnconsumedByUserId(user.getId());

        String token = generateSecureToken();
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setUserId(user.getId());
        verificationToken.setToken(token);
        verificationToken.setExpiresAt(LocalDateTime.now().plusMinutes(ttlMinutes));
        verificationToken.setResendCount(activeToken.map(t -> t.getResendCount() + 1).orElse(1));
        verificationToken.setResendAvailableAt(LocalDateTime.now().plusMinutes(resendCooldownMinutes));
        tokenRepository.save(verificationToken);

        // Enviar novo email
        String verificationLink = frontendBaseUrl + "/verify-email?token=" + token;
        emailService.sendEmailVerification(user.getEmail(), user.getNomeCompleto(), verificationLink);
    }

    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    private String generateSecureToken() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
