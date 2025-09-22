package com.empuje.userservice.service;

import com.empuje.userservice.model.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service

public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend-url:http://localhost:3000}")
    private String frontendUrl;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public void sendUserRegistrationEmail(User user, String plainPassword) {
        String subject = "Bienvenido a Empuje Comunitario - Tus credenciales de acceso";
        String message = String.format(
            "Hola %s,%n%n" +
            "Tu cuenta ha sido creada exitosamente. Aquí están tus credenciales:%n" +
            "Usuario: %s%n" +
            "Contraseña temporal: %s%n%n" +
            "Por favor, inicia sesión y cambia tu contraseña lo antes posible.%n%n" +
            "Puedes iniciar sesión aquí: %s/login%n%n" +
            "Saludos,%nEl equipo de Empuje Comunitario",
            user.getFullName(),
            user.getUsername(),
            plainPassword,
            frontendUrl
        );

        sendEmail(user.getEmail(), subject, message);
    }

    @Async
    public void sendVerificationEmail(User user) {
        if (user.getVerificationToken() == null) {
            throw new IllegalStateException("No verification token found for user");
        }
        
        String verificationUrl = frontendUrl + "/verify-email?token=" + user.getVerificationToken();
        String subject = "Verifica tu correo electrónico - Empuje Comunitario";
        String message = String.format(
            "Hola %s,%n%n" +
            "Por favor haz clic en el siguiente enlace para verificar tu dirección de correo electrónico:%n%n" +
            "%s%n%n" +
            "Si no has creado una cuenta, puedes ignorar este correo.%n%n" +
            "Saludos,%nEl equipo de Empuje Comunitario",
            user.getFullName(),
            verificationUrl
        );
        
        sendEmail(user.getEmail(), subject, message);
    }
    
    @Async
    public void sendPasswordResetEmail(User user, String resetToken) {
        String resetUrl = frontendUrl + "/reset-password?token=" + resetToken;
        String subject = "Restablecer contraseña - Empuje Comunitario";
        String message = String.format(
            "Hola %s,%n%n" +
            "Has solicitado restablecer tu contraseña. Por favor, haz clic en el siguiente enlace para crear una nueva contraseña:%n%n" +
            "%s%n%n" +
            "Si no solicitaste este restablecimiento, por favor ignora este correo.%n%n" +
            "Saludos,%nEl equipo de Empuje Comunitario",
            user.getFullName(),
            resetUrl
        );

        sendEmail(user.getEmail(), subject, message);
    }

    @Async
    public void sendPasswordResetConfirmationEmail(User user) {
        String subject = "Contraseña actualizada - Empuje Comunitario";
        String message = String.format(
            "Hola %s,%n%n" +
            "Tu contraseña ha sido actualizada exitosamente.%n%n" +
            "Si no realizaste este cambio, por favor contacta a soporte inmediatamente.%n%n" +
            "Puedes iniciar sesión aquí: %s/login%n%n" +
            "Saludos,%nEl equipo de Empuje Comunitario",
            user.getFullName(),
            frontendUrl
        );

        sendEmail(user.getEmail(), subject, message);
    }

    @Async
    public void sendEmail(String to, String subject, String content) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(content);
            
            mailSender.send(message);
        } catch (Exception e) {
            // Log the error but don't throw it to avoid interrupting the main flow
            System.err.println("Error al enviar correo a " + to + ": " + e.getMessage());
        }
    }
}
