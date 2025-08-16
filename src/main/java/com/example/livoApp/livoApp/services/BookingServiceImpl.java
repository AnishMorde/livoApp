package com.example.livoApp.livoApp.services;

import com.example.livoApp.livoApp.dto.BookingDto;
import com.example.livoApp.livoApp.dto.BookingRequest;
import com.example.livoApp.livoApp.dto.GuestDto;
import com.example.livoApp.livoApp.entity.*;
import com.example.livoApp.livoApp.entity.enums.BookingStatus;
import com.example.livoApp.livoApp.exception.ResourceNotFoundException;
import com.example.livoApp.livoApp.exception.UnAuthorisedException;
import com.example.livoApp.livoApp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepo bookingRepo;
    private final HotelRepo hotelRepo;
    private final RoomRepo roomRepo;
    private final InvetoryRepo invetoryRepo;
    private final ModelMapper modelMapper;
    private final GuestRepo guestRepo;

    @Override
    @Transactional
    public BookingDto initialiseBooking(BookingRequest bookingRequest) {
        Hotel hotel = hotelRepo.findById(bookingRequest.getHotelId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(STR."Hotel not found with this id \{bookingRequest.getHotelId()}"));

        Room room = roomRepo.findById(bookingRequest.getRoomId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(STR."Room not found with this id \{bookingRequest.getRoomId()}"));

        List<Inventory> inventoryList = invetoryRepo.findAndAvailableInventory(
                bookingRequest.getRoomId(),
                bookingRequest.getCheckInDate(),
                bookingRequest.getEndDate(),
                bookingRequest.getRoomsCount()
        );

        long daysCount = ChronoUnit.DAYS.between(bookingRequest.getCheckInDate(), bookingRequest.getEndDate()) + 1;

        if (inventoryList.size() != daysCount) {
            throw new IllegalStateException("Room is not available anymore");
        }

        for (Inventory inventory : inventoryList) {
            inventory.setReservedCount(inventory.getBookedCount() + bookingRequest.getRoomsCount());
        }

        invetoryRepo.saveAll(inventoryList);



        Booking booking = Booking.builder()
                .bookStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate().atStartOfDay()) // Convert LocalDate to LocalDateTime
                .checkOutDate(bookingRequest.getEndDate().atStartOfDay())   // Convert LocalDate to LocalDateTime
                .user(getCurrentUser())
                .amount(BigDecimal.TEN)
                .roomsCount(bookingRequest.getRoomsCount())
                .build();

        booking = bookingRepo.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    public BookingDto addGuests(Long bookingId, List<GuestDto> guestDtoList) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException(STR."Booking not found with is\{bookingId}"));

        User user = getCurrentUser();
        if( !user.equals(booking.getUser())){
            throw new UnAuthorisedException(STR."Booking does not belong to this user : \{user.getId()}");
        }
        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking is already expired");
        }

        if(booking.getBookStatus() != BookingStatus.RESERVED){
            throw new IllegalStateException("Booking is not under reserved state , cannot add guests");
        }

        for(GuestDto guestDto : guestDtoList){
            Guest guest = modelMapper.map(guestDto , Guest.class);
            guest.setUser(user);
            guest = guestRepo.save(guest);
            booking.getGuests().add(guest);
        }

        booking.setBookStatus(BookingStatus.GUEST_ADDED);
       booking = bookingRepo.save(booking);
        return modelMapper.map(booking , BookingDto.class);
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
}