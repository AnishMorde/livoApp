package com.example.livoApp.livoApp.dto;

import com.example.livoApp.livoApp.entity.HotelContactInfo;
import lombok.Data;


@Data
public class Hoteldto {
    private  Long id;
    private String name;
    private String city;
    private String[] photos;
    private String [] amenities;
    private HotelContactInfo contactInfo;
    private Boolean active;
}
