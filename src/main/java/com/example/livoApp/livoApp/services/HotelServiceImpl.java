package com.example.livoApp.livoApp.services;

import com.example.livoApp.livoApp.dto.HotelInfoDto;
import com.example.livoApp.livoApp.dto.Hoteldto;
import com.example.livoApp.livoApp.dto.Roomdto;
import com.example.livoApp.livoApp.entity.Hotel;
import com.example.livoApp.livoApp.entity.Room;
import com.example.livoApp.livoApp.exception.ResourceNotFoundException;
import com.example.livoApp.livoApp.repository.HotelRepo;
import com.example.livoApp.livoApp.repository.RoomRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelServiceImpl implements HotelService{

    private final HotelRepo hotelRepo;
    private final ModelMapper modelMapper;
    private final InventoryService inventoryService;
    private final RoomRepo roomRepo;

    @Override
    public Hoteldto createNewHotel(Hoteldto hoteldto) {
        log.info("Creating the new Hotel");
        //converts the hoteldto to hotel class
        Hotel hotel = modelMapper.map(hoteldto , Hotel.class);
//        hotel.setActive(false);
        hotelRepo.save(hotel);
        log.info("Created the new Hotel");
       return  modelMapper.map(hotel , Hoteldto.class);
    }

    @Override
    public Hoteldto getHotelById(Long id) {
        Hotel hotel = hotelRepo
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with the ID:" + id));

        return modelMapper.map(hotel , Hoteldto.class);

    }

    @Override
    public Hoteldto updateHotelbyId(Long id , Hoteldto hoteldto) {
        Hotel hotel = hotelRepo
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with this id :" + id) );
        modelMapper.map(hoteldto , hotel);
        hotel.setId(id);
        hotel = hotelRepo.save(hotel);
        return modelMapper.map(hotel , Hoteldto.class);
    }


    @Override
    @Transactional
    public void deleteHotelById(Long id) {
        Hotel hotel = hotelRepo
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with this id :" + id) );


        for(Room room : hotel.getRooms()){
            inventoryService.deleteAllInventories(room);
            roomRepo.deleteById(room.getId());
        }

        hotelRepo.deleteById(id);

    }

    @Override
    @Transactional
    public void activateHotel(Long id) {
        log.info("Activating the hotel id{}", id);
        Hotel hotel = hotelRepo
                .findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Hotel not found with this id :" + id) );
        hotel.setActive(true);
        hotelRepo.save(hotel);

        for(Room room : hotel.getRooms()){
            inventoryService.initializeRoomForAYear(room);
        }

    }

    @Override
    public HotelInfoDto getHotelInfoById(Long id) {
        Hotel hotel = hotelRepo
                .findById(id)
                .orElseThrow(()->new ResourceNotFoundException("Hotel not found with the ID:" + id));

        List<Roomdto> rooms = hotel.getRooms()
                .stream()
                .map(element -> modelMapper.map(element , Roomdto.class))
                .toList();

        return new HotelInfoDto(modelMapper.map(hotel , Hoteldto.class) , rooms);

    }


}



























