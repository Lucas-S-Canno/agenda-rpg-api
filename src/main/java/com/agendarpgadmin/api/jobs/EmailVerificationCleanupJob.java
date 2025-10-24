package com.agendarpgadmin.api.jobs;

import com.agendarpgadmin.api.services.Public.EmailVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailVerificationCleanupJob {

    @Autowired
    private EmailVerificationService emailVerificationService;

    @Scheduled(fixedRate = 300000) // Executa a cada 5 minutos
    public void cleanupExpiredTokens() {
        emailVerificationService.cleanupExpiredTokens();
    }
}
