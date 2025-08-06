package com.example.livoApp.livoApp.services;

import com.example.livoApp.livoApp.dto.HotelPriceDto;
import com.example.livoApp.livoApp.dto.HotelSearchDto;
import com.example.livoApp.livoApp.dto.Hoteldto;
import com.example.livoApp.livoApp.entity.Hotel;
import com.example.livoApp.livoApp.entity.HotelMinPrice;
import com.example.livoApp.livoApp.entity.Inventory;
import com.example.livoApp.livoApp.entity.Room;
import com.example.livoApp.livoApp.repository.HotelMinPriceRepo;
import com.example.livoApp.livoApp.repository.InvetoryRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl implements InventoryService {
    private final InvetoryRepo invetoryRepo;
    private final ModelMapper modelMapper;
    private final HotelMinPriceRepo hotelMinPriceRepo;
    @Override
    public void initializeRoomForAYear(Room room) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusYears(1);
        for (; !today.isAfter(endDate); today = today.plusDays(1)) {
            Inventory inventory = Inventory.builder()
                    .hotel(room.getHotel())
                    .room(room)
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .date(today)
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .reservedCount(0)
                    .closed(false)
                    .build();

            invetoryRepo.save(inventory);
        }
    }

    @Override
    public void deleteAllInventories(Room room) {
        invetoryRepo.deleteByRoom(room);
    }

    @Override
    public Page<HotelPriceDto> searchHotel(HotelSearchDto hotelSearchDto) {
        // Validate input dates
        if (hotelSearchDto.getCheckInDate() == null || hotelSearchDto.getEndDate() == null
                || hotelSearchDto.getCheckInDate().isAfter(hotelSearchDto.getEndDate())) {
            throw new IllegalArgumentException("Invalid check-in or end date");
        }
        if (hotelSearchDto.getRoomsCount() == null || hotelSearchDto.getRoomsCount() <= 0) {
            throw new IllegalArgumentException("Rooms count must be greater than zero");
        }

        Pageable pageable = PageRequest.of(hotelSearchDto.getPage(), hotelSearchDto.getSize());
        long dateCount = ChronoUnit.DAYS.between(hotelSearchDto.getCheckInDate(), hotelSearchDto.getEndDate()) + 1;

        Page<HotelPriceDto> hotelPage = hotelMinPriceRepo.findHotelsWithAvailableInventory(
                hotelSearchDto.getCity(),
                hotelSearchDto.getCheckInDate(),
                hotelSearchDto.getEndDate(),
                pageable
        );

        return hotelPage;
    }
}