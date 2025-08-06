package com.example.livoApp.livoApp.services;


import com.example.livoApp.livoApp.dto.HotelPriceDto;
import com.example.livoApp.livoApp.dto.HotelSearchDto;
import com.example.livoApp.livoApp.dto.Hoteldto;
import com.example.livoApp.livoApp.entity.Room;
import org.springframework.data.domain.Page;

public interface InventoryService  {

    void initializeRoomForAYear(Room roomId);

    void deleteAllInventories(Room room);

    Page<HotelPriceDto> searchHotel(HotelSearchDto hotelSearchDto);
}
