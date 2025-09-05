package com.example.livoApp.livoApp.controller;

import com.example.livoApp.livoApp.dto.BookingDto;
import com.example.livoApp.livoApp.dto.BookingRequest;
import com.example.livoApp.livoApp.dto.GuestDto;
import com.example.livoApp.livoApp.services.BookingService;
import com.stripe.exception.StripeException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    @PostMapping("/init")
    public ResponseEntity<BookingDto> initialiseBooking(@RequestBody BookingRequest bookingRequest){
        return ResponseEntity.ok(bookingService.initialiseBooking(bookingRequest));
    }

    @PostMapping("/{bookingId}/addGuests")
    public ResponseEntity<BookingDto> addGuests(@PathVariable Long bookingId ,
            @RequestBody List<GuestDto> guestDtoList){
        return  ResponseEntity.ok(bookingService.addGuests(bookingId , guestDtoList));
    }

    @PostMapping("/{bookingId}/payments")
    public ResponseEntity<Map<String , String>> initiatePayment(@PathVariable Long bookingId){
        String sessionUrl = bookingService.initiatePayment(bookingId );
        return ResponseEntity.ok(Map.of("sessionUrl" , sessionUrl));

    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long bookingId) throws StripeException {
        bookingService.cancelBooking(bookingId );
        return ResponseEntity.noContent().build();

    }

    @PostMapping("/{bookingId}/status")
    public ResponseEntity<Map<String , String>> getBookingStatus(@PathVariable Long bookingId) throws StripeException {
        return ResponseEntity.ok( Map.of("status" , bookingService.getBookingStatus(bookingId )));

    }


}
