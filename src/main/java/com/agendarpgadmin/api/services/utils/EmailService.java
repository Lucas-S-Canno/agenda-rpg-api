package com.agendarpgadmin.api.services.utils;

import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final Resend resend;
    private final String fromEmail;

    public EmailService(
            @Value("${resend.api-key}") String apiKey,
            @Value("${resend.from-email}") String fromEmail
    ) {
        this.resend = new Resend(apiKey);
        this.fromEmail = fromEmail;
    }

    public void sendResetCode(String email, String code) {
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(email)
                    .subject("Codigo de Recuperacao de Senha")
                    .html(buildResetCodeHtml(code))
                    .build();

            CreateEmailResponse response = resend.emails().send(request);
            log.info("Email de reset enviado para {} com id {}", email, response.getId());
        } catch (Exception e) {
            log.error("Erro ao enviar email de reset para {}", email, e);
        }
    }

    public void sendPasswordChangeCode(String email, String code) {
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(email)
                    .subject("Codigo de Confirmacao para Troca de Senha")
                    .html(buildPasswordChangeCodeHtml(code))
                    .build();

            CreateEmailResponse response = resend.emails().send(request);
            log.info("Email de confirmacao de troca de senha enviado para {} com id {}", email, response.getId());
        } catch (Exception e) {
            log.error("Erro ao enviar email de confirmacao de troca de senha para {}", email, e);
        }
    }

    public void sendEmailVerification(String email, String nomeCompleto, String verificationLink) {
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(email)
                    .subject("Verificacao de Email - Agenda RPG")
                    .html(buildVerificationHtml(nomeCompleto, verificationLink))
                    .build();

            CreateEmailResponse response = resend.emails().send(request);
            log.info("Email de verificacao enviado para {} com id {}", email, response.getId());
        } catch (Exception e) {
            log.error("Erro ao enviar email de verificacao para {}", email, e);
        }
    }

    private String buildResetCodeHtml(String code) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 24px;">
                  <h2 style="color: #1f2937;">Recuperacao de Senha</h2>
                  <p>Seu codigo de recuperacao e:</p>
                  <div style="font-size: 32px; font-weight: bold; letter-spacing: 8px; padding: 16px;
                              background: #f3f4f6; border-radius: 8px; text-align: center; color: #4f46e5;">
                    %s
                  </div>
                  <p>Este codigo expira em <strong>15 minutos</strong>.</p>
                  <p style="color: #6b7280; font-size: 13px;">Se voce nao solicitou esta recuperacao, ignore este email.</p>
                </div>
                """.formatted(code);
    }

    private String buildPasswordChangeCodeHtml(String code) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 24px;">
                  <h2 style="color: #1f2937;">Confirmacao de Troca de Senha</h2>
                  <p>Use o codigo abaixo para confirmar a troca de senha da sua conta:</p>
                  <div style="font-size: 32px; font-weight: bold; letter-spacing: 8px; padding: 16px;
                              background: #f3f4f6; border-radius: 8px; text-align: center; color: #4f46e5;">
                    %s
                  </div>
                  <p>Este codigo expira em <strong>10 minutos</strong>.</p>
                  <p style="color: #6b7280; font-size: 13px;">Se voce nao solicitou esta acao, ignore este email.</p>
                </div>
                """.formatted(code);
    }

    private String buildVerificationHtml(String nomeCompleto, String verificationLink) {
        return """
                <div style="font-family: Arial, sans-serif; max-width: 480px; margin: 0 auto; padding: 24px;">
                  <h2 style="color: #1f2937;">Ola, %s!</h2>
                  <p>Obrigado por se cadastrar no <strong>Agenda RPG</strong>!</p>
                  <p>Clique no botao abaixo para ativar sua conta:</p>
                  <a href="%s" style="display: inline-block; padding: 12px 24px; background: #4f46e5;
                     color: white; text-decoration: none; border-radius: 8px; font-weight: bold;">
                    Verificar Email
                  </a>
                  <p style="color: #6b7280; font-size: 13px; margin-top: 16px;">Este link expira em 1 hora.</p>
                  <p style="color: #9ca3af; font-size: 12px;">Caso o botao nao funcione, use este link: %s</p>
                </div>
                """.formatted(nomeCompleto, verificationLink, verificationLink);
    }
}
