package com.example.livoApp.livoApp.services;


import com.example.livoApp.livoApp.dto.Roomdto;

import java.util.List;

public interface RoomService {

    Roomdto creatNewRoom( Long hotelId, Roomdto roomdto);

    List<Roomdto> getAllRoomsInHotel(Long hotelId);

    Roomdto getRoomById(Long id);

    void deleteRoomById(Long id);

}
