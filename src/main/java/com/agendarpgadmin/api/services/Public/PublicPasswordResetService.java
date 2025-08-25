package com.agendarpgadmin.api.services.Public;

import com.agendarpgadmin.api.entities.PasswordResetCodeEntity;
import com.agendarpgadmin.api.repositories.PasswordResetCodeRepository;
import com.agendarpgadmin.api.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PublicPasswordResetService {

    @Autowired
    private PasswordResetCodeRepository codeRepository;

    @Autowired
    private UserRepository userRepository; // supondo que já existe

    public void requestResetCode(String email) {
        String code = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        // Log temporário para teste
        System.out.println("CÓDIGO GERADO PARA " + email + ": " + code);

        PasswordResetCodeEntity entity = new PasswordResetCodeEntity();
        entity.setEmail(email);
        entity.setCode(code);
        entity.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        entity.setUsed(false);
        codeRepository.save(entity);
        // Aqui você pode disparar o envio do e-mail futuramente
    }

    public String validateResetCode(String email, String code) {
        String normalized = code == null ? "" : code.trim().toUpperCase();
        var entityOpt = codeRepository.findByEmailAndCodeAndUsedFalse(email, normalized);
        if (entityOpt.isPresent() && entityOpt.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            String token = UUID.randomUUID().toString();
            PasswordResetCodeEntity entity = entityOpt.get();
            entity.setResetToken(token);
            codeRepository.save(entity);
            return token;
        }
        throw new RuntimeException("Código inválido ou expirado");
    }

    public void resetPassword(String email, String newPassword, String resetToken) {
        var entityOpt = codeRepository.findByEmailAndResetTokenAndUsedFalse(email, resetToken);
        if (entityOpt.isPresent() && entityOpt.get().getExpiresAt().isAfter(LocalDateTime.now())) {
            // Atualizar senha do usuário
            var userOpt = userRepository.findByEmail(email);
            if (userOpt.isPresent()) {
                var user = userOpt.get();
                user.setPassword(newPassword); // idealmente, faça hash da senha
                userRepository.save(user);
                var entity = entityOpt.get();
                entity.setUsed(true);
                codeRepository.save(entity);
            } else {
                throw new RuntimeException("Usuário não encontrado");
            }
        } else {
            throw new RuntimeException("Token inválido ou expirado");
        }
    }

}
