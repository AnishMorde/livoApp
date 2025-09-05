package com.example.livoApp.livoApp.repository;

import com.example.livoApp.livoApp.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingRepo extends JpaRepository<Booking , Long> {
    Optional<Booking> findByStripeSessionId(String stripeSessionId);

}
