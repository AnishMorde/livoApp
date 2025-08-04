package com.example.livoApp.livoApp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    private Long hotelId;
    private Long roomId;
    private LocalDate checkInDate;
    private LocalDate endDate;
    private Integer roomsCount;


}
