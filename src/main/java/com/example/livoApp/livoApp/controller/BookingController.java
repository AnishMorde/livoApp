package com.example.livoApp.livoApp.controller;

import com.example.livoApp.livoApp.dto.BookingDto;
import com.example.livoApp.livoApp.dto.BookingRequest;
import com.example.livoApp.livoApp.dto.GuestDto;
import com.example.livoApp.livoApp.services.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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


}
