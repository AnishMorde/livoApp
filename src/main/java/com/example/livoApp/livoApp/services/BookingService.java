package com.example.livoApp.livoApp.services;

import com.example.livoApp.livoApp.dto.BookingDto;
import com.example.livoApp.livoApp.dto.BookingRequest;
import com.example.livoApp.livoApp.dto.GuestDto;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;

import java.util.List;
import java.util.Map;

public interface BookingService {
    BookingDto initialiseBooking(BookingRequest bookingRequest);

    BookingDto addGuests(Long bookingId, List<GuestDto> guestDtos);

    String initiatePayment(Long bookingId);

    void capturePayment(Event event);

    void cancelBooking(Long bookingId) throws StripeException;

    String getBookingStatus(Long bookingId);
}
