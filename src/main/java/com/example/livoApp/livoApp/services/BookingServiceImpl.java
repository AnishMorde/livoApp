package com.example.livoApp.livoApp.services;

import com.example.livoApp.livoApp.dto.BookingDto;
import com.example.livoApp.livoApp.dto.BookingRequest;
import com.example.livoApp.livoApp.dto.GuestDto;
import com.example.livoApp.livoApp.entity.*;
import com.example.livoApp.livoApp.entity.enums.BookingStatus;
import com.example.livoApp.livoApp.exception.ResourceNotFoundException;
import com.example.livoApp.livoApp.exception.UnAuthorisedException;
import com.example.livoApp.livoApp.repository.*;
import com.example.livoApp.livoApp.strategy.PricingService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.Refund;
import com.stripe.model.checkout.Session;
import com.stripe.param.RefundCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

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
    private final CheckOutService checkOutService;
    private final PricingService pricingService;
    private final  EmailService emailService;

    @Value("${frontend.url}")
    private String frontEndUrl;

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
            inventory.setReservedCount(inventory.getReservedCount() + bookingRequest.getRoomsCount());
        }

        invetoryRepo.saveAll(inventoryList);


       BigDecimal totalPriceForOneRoom = pricingService.calculateTotalPrice(inventoryList);
       BigDecimal totalPrice = totalPriceForOneRoom.multiply(BigDecimal.valueOf(bookingRequest.getRoomsCount()));






        Booking booking = Booking.builder()
                .bookStatus(BookingStatus.RESERVED)
                .hotel(hotel)
                .room(room)
                .checkInDate(bookingRequest.getCheckInDate()) // Convert LocalDate to LocalDateTime
                .checkOutDate(bookingRequest.getEndDate())   // Convert LocalDate to LocalDateTime
                .user(getCurrentUser())
                .amount(totalPrice)
                .roomsCount(bookingRequest.getRoomsCount())
                .build();

        booking = bookingRepo.save(booking);
        return modelMapper.map(booking, BookingDto.class);
    }

    @Override
    @Transactional
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

    @Override
    @Transactional
    public String initiatePayment(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException(STR."Booking not found with is\{bookingId}"));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException(STR."Booking does not belong to this user : \{user.getId()}");
        }

        if(hasBookingExpired(booking)){
            throw new IllegalStateException("Booking is already expired");
        }

    String sessionUrl = checkOutService.getCheckOutSession(booking , STR."\{frontEndUrl}/success", STR."\{frontEndUrl}/failure");
        booking.setBookStatus(BookingStatus.PAYMENT_PENDING);
        bookingRepo.save(booking);
        return sessionUrl;

    }

    @Override
    @Transactional
    public void capturePayment(Event event) {
        if ("checkout.session.completed".equals(event.getType())) {
            try {
                Session session = (Session) event.getDataObjectDeserializer().getObject().orElse(null);
                if (session == null) return;

                String sessionId = session.getId();
                Booking booking = bookingRepo.findByStripeSessionId(sessionId)
                        .orElseThrow(() -> new ResourceNotFoundException(
                                STR."Booking Not found for the session id \{sessionId}"));

                booking.setBookStatus(BookingStatus.CONFIRMED);
                bookingRepo.save(booking);

                invetoryRepo.findAndLockReservedInventory(
                        booking.getRoom().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getRoomsCount());

                invetoryRepo.confirmBooking(
                        booking.getRoom().getId(),
                        booking.getCheckInDate(),
                        booking.getCheckOutDate(),
                        booking.getRoomsCount());


                try {
                    User user = booking.getUser();
                    if (user.getEmail() != null) {
                        String subject = STR."Booking Confirmed - \{booking.getHotel().getName()}";

                        String body = STR."""
                            Dear \{user.getName()},

                            🎉 Your booking has been confirmed successfully!

                            📌 Hotel: \{booking.getHotel().getName()}
                            🏨 Room: \{booking.getRoom().getType()}
                            🛏️ Total Rooms Booked: \{booking.getRoomsCount()}

                            📅 Check-in: \{booking.getCheckInDate()}
                            📅 Check-out: \{booking.getCheckOutDate()}

                            💰 Total Amount: ₹\{booking.getAmount()}

                            Thank you for booking with us!
                            We look forward to hosting you.
                            """;

                        emailService.sendBookingConfirmation(user.getEmail(), subject, body);
                        log.info("Booking confirmation email sent to {}", user.getEmail());
                    } else {
                        log.warn("User email is null, cannot send booking confirmation.");
                    }
                } catch (Exception e) {
                    log.error("Failed to send booking email: {}", e.getMessage(), e);
                }

            } catch (Exception e) {
                log.error("Error processing Stripe event {}: {}", event.getId(), e.getMessage(), e);
            }
        }
    }


    @Override
    @Transactional
    public void cancelBooking(Long bookingId) throws StripeException {

        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException(STR."Booking not found with is\{bookingId}"));

        User user = getCurrentUser();
        if(!user.equals(booking.getUser())){
            throw new UnAuthorisedException(STR."Booking does not belong to this user : \{user.getId()}");
        }

        if(booking.getBookStatus() != BookingStatus.CONFIRMED){
            throw new IllegalStateException("only confirmed bookings can be cancelled");
        }

        booking.setBookStatus(BookingStatus.CANCELLED);
        bookingRepo.save(booking);
        invetoryRepo.findAndLockReservedInventory(booking.getRoom().getId(), booking.getCheckInDate() , booking.getCheckOutDate() , booking.getRoomsCount());
        invetoryRepo.cancelBooking(booking.getRoom().getId(), booking.getCheckInDate() , booking.getCheckOutDate() , booking.getRoomsCount());

       try{
           Session session  =  Session.retrieve(booking.getStripeSessionId());
           RefundCreateParams refundParams = RefundCreateParams.builder()
                   .setPaymentIntent(session.getPaymentIntent())

                   .build();

           Refund.create(refundParams);
       }catch (StripeException e){
              log.error(STR."Stripe exception while cancelling the booking id \{bookingId} error : \{e.getMessage()}");
              throw e;
       }


    }

    @Override
    public String getBookingStatus(Long bookingId) {
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(()->new ResourceNotFoundException(STR."Booking not found with is\{bookingId}"));

        User user = getCurrentUser();
        if( !user.equals(booking.getUser())){
            throw new UnAuthorisedException(STR."Booking does not belong to this user : \{user.getId()}");
        }

        return booking.getBookStatus().name();


    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public boolean hasBookingExpired(Booking booking){
        return booking.getCreatedAt().plusMinutes(10).isBefore(LocalDateTime.now());
    }
}