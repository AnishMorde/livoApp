package com.example.livoApp.livoApp.services;


import com.example.livoApp.livoApp.entity.Booking;
import com.example.livoApp.livoApp.entity.User;
import com.example.livoApp.livoApp.repository.BookingRepo;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class CheckOutServiceImpl implements CheckOutService{

    private final BookingRepo bookingRepo;
    @Override
    public String getCheckOutSession(Booking booking, String successUrl, String failureUrl) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        try {
            CustomerCreateParams customerCreateParams =  CustomerCreateParams.builder()
                    .setName(user.getName())
                    .setEmail(user.getEmail())
                    .build();

            Customer customer = Customer.create(customerCreateParams);

            SessionCreateParams params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setBillingAddressCollection(SessionCreateParams.BillingAddressCollection.REQUIRED)
                    .setCustomer(customer.getId())
                    .setSuccessUrl(successUrl)
                    .setCancelUrl(failureUrl)
                    .addLineItem(
                           SessionCreateParams.LineItem.builder()
                                   .setQuantity(1L)
                                   .setPriceData(
                                           SessionCreateParams.LineItem.PriceData.builder()
                                                   .setCurrency("inr")
                                                   .setUnitAmount(booking.getAmount().multiply(BigDecimal.valueOf(100)).longValue())
                                                   .setProductData(
                                                          SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                  .setName(STR."\{booking.getHotel().getName()} : \{booking.getRoom().getType()}")
                                                                  .setDescription(STR."Booking id \{booking.getId()}")
                                                                  .build()
                                                   )
                                                   .build()
                                   ).build()
                    )
                    .build();

            Session session = Session.create(params);

            booking.setStripeSessionId(session.getId());
            bookingRepo.save(booking);

            return session.getUrl();
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }

    }


}
