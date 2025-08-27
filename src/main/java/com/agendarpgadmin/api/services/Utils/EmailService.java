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

    private String buildEmailContent(String code) {
        return "Olá!\n\n" +
                "Seu código de recuperação de senha é: " + code + "\n\n" +
                "Este código expira em 15 minutos.\n\n" +
                "Se você não solicitou esta recuperação, ignore este email.\n\n" +
                "Atenciosamente,\n" +
                "Equipe do Sistema";
    }

}
