package com.example.livoApp.livoApp.entity;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
//embeddable  use beacuse we taken all this from the HotelContactInfo and used in the Hotel
@Embeddable
public class HotelContactInfo {
    private String address;
    private String phoneNumber;
    private String email;
    private String location;
}
