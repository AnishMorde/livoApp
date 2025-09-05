package com.example.livoApp.livoApp.controller;

import com.example.livoApp.livoApp.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class EmailController {

    private final EmailService emailService;

    @PostMapping("/test-email")
    public ResponseEntity<Map<String , String>> testEmail(@RequestBody String email) {
        try {
            emailService.sendBookingConfirmation(
                    "adityamarkad55@gmail.com",
                    "Test Email",
                    "This is a test email from your booking system."
            );
            return ResponseEntity.ok(Map.of("Email sent successfully" , "Email sent successfully"));
        } catch (Exception e) {
            return ResponseEntity.ok(Map.of("Email sent successfully" , e.getMessage()));
        }
    }
}