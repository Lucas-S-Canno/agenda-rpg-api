package com.agendarpgadmin.api.services.utils;
import com.resend.Resend;
import com.resend.services.emails.model.CreateEmailOptions;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final Resend resend;

    public EmailService(@Value("${resend.api-key}") String apiKey) {
        this.resend = new Resend(apiKey);
    }

    @Value("${resend.from-email}")
    private String fromEmail;

    public void sendResetCode(String email, String code) {
        sendTextEmail(email, "Código de Recuperação de Senha", buildEmailContent(code));
    }

    public void sendEmailVerification(String email, String nomeCompleto, String verificationLink) {
        sendTextEmail(email, "Verificação de Email - Agenda RPG", buildVerificationEmailContent(nomeCompleto, verificationLink));
    }

    private void sendTextEmail(String to, String subject, String body) {
        try {
            CreateEmailOptions request = CreateEmailOptions.builder()
                    .from(fromEmail)
                    .to(to)
                    .subject(subject)
                    .text(body)
                    .build();

            resend.emails().send(request);
            log.info("Email enviado com sucesso para: {}", to);

        } catch (RuntimeException e) {
            if (isSandboxRestriction(e)) {
                log.error("Resend bloqueou envio para '{}' por conta sandbox. Para enviar para destinatarios reais, verifique um dominio em resend.com/domains e use resend.from-email com esse dominio.", to);
            }

            log.error("Erro ao enviar email para {}", to, e);
        } catch (Exception e) {
            log.error("Erro inesperado ao enviar email para {}", to, e);
        }
    }

    private boolean isSandboxRestriction(RuntimeException e) {
        String message = e.getMessage();
        return message != null && message.contains("You can only send testing emails to your own email address");
    }

    private String buildEmailContent(String code) {
        return "Olá!\n\n" +
                "Seu código de recuperação de senha é: " + code + "\n\n" +
                "Este código expira em 15 minutos.\n\n" +
                "Se você não solicitou esta recuperação, ignore este email.\n\n" +
                "Atenciosamente,\n" +
                "Equipe do Sistema";
    }

    private String buildVerificationEmailContent(String nomeCompleto, String verificationLink) {
        return "Olá " + nomeCompleto + "!\n\n" +
                "Obrigado por se cadastrar no Agenda RPG!\n\n" +
                "Para ativar sua conta, clique no link abaixo:\n" +
                verificationLink + "\n\n" +
                "Este link expira em 1 hora.\n\n" +
                "Se você não se cadastrou em nossa plataforma, ignore este email.\n\n" +
                "Caso tenha problemas com o link, copie e cole o endereço completo no seu navegador.\n\n" +
                "Atenciosamente,\n" +
                "Equipe Agenda RPG";
    }
}
