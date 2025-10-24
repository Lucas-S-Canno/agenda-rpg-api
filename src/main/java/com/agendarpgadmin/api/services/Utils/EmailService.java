package com.agendarpgadmin.api.services.Utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendResetCode(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Código de Recuperação de Senha");
            message.setText(buildEmailContent(code));

            emailSender.send(message);
            System.out.println("Email enviado com sucesso para: " + email);

        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            //TO-DO: Em produção, tem que logar o erro mas não quebrar o fluxo
        }
    }

    public void sendEmailVerification(String email, String nomeCompleto, String verificationLink) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(email);
            message.setSubject("Verificação de Email - Agenda RPG");
            message.setText(buildVerificationEmailContent(nomeCompleto, verificationLink));

            emailSender.send(message);
            System.out.println("Email de verificação enviado com sucesso para: " + email);

        } catch (Exception e) {
            System.err.println("Erro ao enviar email de verificação: " + e.getMessage());
            //TO-DO: Em produção, tem que logar o erro mas não quebrar o fluxo
        }
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
