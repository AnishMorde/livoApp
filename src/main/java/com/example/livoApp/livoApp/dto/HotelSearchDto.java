package com.example.livoApp.livoApp.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class HotelSearchDto {
    private String city;
    private LocalDate checkInDate;
    private LocalDate endDate;

    private Integer roomsCount;

    private Integer page =0;
    private Integer size =10;

}
