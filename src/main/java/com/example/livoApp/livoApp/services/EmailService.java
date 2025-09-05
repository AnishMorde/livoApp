package com.example.livoApp.livoApp.services;

public interface EmailService {
    void sendBookingConfirmation(String to, String subject, String body);
}
