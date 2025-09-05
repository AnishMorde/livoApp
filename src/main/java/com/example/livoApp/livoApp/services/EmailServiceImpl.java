package com.example.livoApp.livoApp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendBookingConfirmation(String toEmail, String subject, String body) {
        try {
            log.info("Attempting to send email to: {}", toEmail);
            log.info("Email subject: {}", subject);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);

            // Set from email (important for Gmail)
            message.setFrom("your-email@gmail.com"); // Replace with your actual Gmail

            mailSender.send(message);

            log.info("Email sent successfully to: {}", toEmail);

        } catch (MailException e) {
            log.error("Failed to send email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send booking confirmation email", e);
        } catch (Exception e) {
            log.error("Unexpected error while sending email to: {}. Error: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Unexpected error occurred while sending email", e);
        }
    }
}