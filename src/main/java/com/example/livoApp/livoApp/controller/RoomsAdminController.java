package com.example.livoApp.livoApp.controller;

import com.example.livoApp.livoApp.dto.Roomdto;
import com.example.livoApp.livoApp.services.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/hotels/{hotelId}/rooms")
@Slf4j
@RequiredArgsConstructor
public class RoomsAdminController {

    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<Roomdto> createNewRoom(@PathVariable Long hotelId , @RequestBody Roomdto roomdto){
     roomdto =  roomService.creatNewRoom(hotelId, roomdto);
      return new ResponseEntity<>(roomdto , HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Roomdto>> getAllRoomsInHotel(@PathVariable  Long hotelId){
       List<Roomdto> rooms = roomService.getAllRoomsInHotel(hotelId);
       return new ResponseEntity<>(rooms , HttpStatus.OK);
    }

    @GetMapping("/{roomId}")
   public ResponseEntity<Roomdto> getRoomById(@PathVariable Long roomId){
       Roomdto roomdto = roomService.getRoomById(roomId);
       return new ResponseEntity<>(roomdto , HttpStatus.OK);

   }

   @DeleteMapping("/{roomId}")
   public  ResponseEntity<Void> deleteRoomById( @PathVariable Long roomId){
      roomService.deleteRoomById(roomId);
      return ResponseEntity.noContent().build();
  }

}
