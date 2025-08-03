package com.example.livoApp.livoApp.controller;


import com.example.livoApp.livoApp.dto.Hoteldto;
import com.example.livoApp.livoApp.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;


    @PostMapping
    public ResponseEntity<Hoteldto> creatNewHotel(@RequestBody Hoteldto hoteldto){
        log.info("attempting to create new Hotel");
        Hoteldto hoteldto1 = hotelService.createNewHotel(hoteldto);
        return new ResponseEntity<> (hoteldto1 , HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<Hoteldto> getHotelById(@PathVariable Long hotelId){
       Hoteldto hoteldto = hotelService.getHotelById(hotelId);
       return ResponseEntity.ok(hoteldto);
    }

    @PutMapping("/{hotelId}")
    public ResponseEntity<Hoteldto> updateHotelById(@PathVariable Long hotelId ,
                                                    @RequestBody Hoteldto hoteldto){
        Hoteldto hotel = hotelService.updateHotelbyId(hotelId , hoteldto);
          return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable Long hotelId){
        hotelService.deleteHotelById(hotelId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{hotelId}")
    public ResponseEntity<Void>activateHotel(@PathVariable Long hotelId){
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }

}
