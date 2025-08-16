package com.example.livoApp.livoApp.services;

import com.example.livoApp.livoApp.dto.Roomdto;
import com.example.livoApp.livoApp.entity.Hotel;
import com.example.livoApp.livoApp.entity.Room;
import com.example.livoApp.livoApp.entity.User;
import com.example.livoApp.livoApp.exception.ResourceNotFoundException;
import com.example.livoApp.livoApp.exception.UnAuthorisedException;
import com.example.livoApp.livoApp.repository.HotelRepo;
import com.example.livoApp.livoApp.repository.RoomRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {

    private final RoomRepo roomRepo;
    private final ModelMapper modelMapper;
    private final HotelRepo hotelRepo;
    private final InventoryService inventoryService;

    @Override
    @Transactional
    public Roomdto creatNewRoom(Long hotelId, Roomdto roomdto) {
        log.info("Creating the new Room in the Hotel id:{}", hotelId);
        Hotel hotel = hotelRepo
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with this id :" + hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if( !user.equals(hotel.getOwner())){
            throw new UnAuthorisedException(STR."This user does not own this hotel id : \{hotelId}");
        }

        Room room = modelMapper.map(roomdto, Room.class);
        room.setHotel(hotel);
        log.info("Created the new Room in the Hotel id:{}", hotelId);
        room = roomRepo.save(room);

        if (hotel.getActive()) {
            inventoryService.initializeRoomForAYear(room);
        }

        return modelMapper.map(room, Roomdto.class);
    }

    @Override
    public List<Roomdto> getAllRoomsInHotel(Long hotelId) {
        log.info("Getting the All rooms in the Hotel with Hotel Id :{}", hotelId);
        Hotel hotel = hotelRepo
                .findById(hotelId)
                .orElseThrow(() -> new ResourceNotFoundException("Hotel not found with this id :" + hotelId));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if( !user.equals(hotel.getOwner())){
            throw new UnAuthorisedException(STR."This user does not own this hotel id : \{hotelId}");
        }

        log.info("Get the All rooms in the Hotel with Hotel Id :" + hotelId);
        return hotel.getRooms()
                .stream()
                .map((element) -> modelMapper.map(element, Roomdto.class))
                .collect(Collectors.toList());


    }

    @Override
    public Roomdto getRoomById(Long id) {
        log.info("Getting the room by Id :{}", id);

        Room room = roomRepo
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Room not found with this id :" + id));
        return modelMapper.map(room, Roomdto.class);
    }

    @Override
    @Transactional
    public void deleteRoomById(Long roomId) {
        log.info("Deleting the room by id{}", roomId);
        Room room = roomRepo
                .findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException(STR."Room not found with this id :\{roomId}"));

        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if( !user.equals(room.getHotel().getOwner())){
            throw new UnAuthorisedException(STR."This user does not own this room id : \{roomId}");
        }
        inventoryService.deleteAllInventories(room);
        log.info("Delete the room by id{}", roomId);
        roomRepo.deleteById(roomId);


    }
}
