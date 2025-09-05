package com.example.livoApp.livoApp.services;

import com.example.livoApp.livoApp.entity.Booking;

public interface CheckOutService {

    String getCheckOutSession(Booking booking, String successUrl , String failureUrl);
}
