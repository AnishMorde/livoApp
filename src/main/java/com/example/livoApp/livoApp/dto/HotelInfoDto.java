package com.example.livoApp.livoApp.dto;

import com.example.livoApp.livoApp.entity.Room;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotelInfoDto {
    private Hoteldto hotel;
    private List<Roomdto> rooms;
}
