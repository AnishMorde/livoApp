package com.example.livoApp.livoApp.controller;


import com.example.livoApp.livoApp.services.BookingService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/webhook")
@RequiredArgsConstructor
public class WebhookController {

    private final BookingService bookingService;
    @Value("${stripe.webhook.secrete}")
    private String endPointSecrete;

    @PostMapping("/payment")
    public ResponseEntity<Void> capturePayments(@RequestBody String payLoad ,
                                                @RequestHeader("Stripe-Signature") String signatureheader){
        try {
            Event event = Webhook.constructEvent(payLoad , signatureheader , endPointSecrete);
            bookingService.capturePayment(event);
            return ResponseEntity.noContent().build();
        } catch (SignatureVerificationException e) {
            throw new RuntimeException(e);
        }

    }


}
