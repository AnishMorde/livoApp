package com.example.livoApp.livoApp.controller;

import com.example.livoApp.livoApp.dto.HotelInfoDto;
import com.example.livoApp.livoApp.dto.HotelPriceDto;
import com.example.livoApp.livoApp.dto.HotelSearchDto;
import com.example.livoApp.livoApp.dto.Hoteldto;
import com.example.livoApp.livoApp.services.HotelService;
import com.example.livoApp.livoApp.services.InventoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/hotels")
@RequiredArgsConstructor
public class HotelBrowseCotroller {

    private final InventoryService inventoryService;
    private final HotelService hotelService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelPriceDto>> searchHotel(@RequestBody HotelSearchDto hotelSearchDto){
       var page  = inventoryService.searchHotel(hotelSearchDto);
       return ResponseEntity.ok(page);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId){
        return ResponseEntity.ok(hotelService.getHotelInfoById(hotelId));
    }



}
