package com.example.livoApp.livoApp.services;

import com.example.livoApp.livoApp.dto.HotelInfoDto;
import com.example.livoApp.livoApp.dto.Hoteldto;

public interface HotelService {
     Hoteldto createNewHotel(Hoteldto hoteldto);
     Hoteldto  getHotelById(Long id);
     Hoteldto updateHotelbyId(Long id ,  Hoteldto hoteldto);
     void deleteHotelById(Long id);
     void activateHotel(Long hotelId);

     HotelInfoDto getHotelInfoById(Long hotelId);
}
