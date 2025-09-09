package com.empuje.userservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void sendEmail_ShouldSendEmailSuccessfully() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Email Body";

        // Act
        emailService.sendEmail(to, subject, text);

        // Assert
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_ShouldHandleExceptionGracefully() {
        // Arrange
        String to = "test@example.com";
        String subject = "Test Subject";
        String text = "Test Email Body";

        doThrow(new RuntimeException("Mail server error")).when(mailSender).send(any(SimpleMailMessage.class));

        // Act & Assert
        emailService.sendEmail(to, subject, text);
        
        // Verify the method was still called
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}
