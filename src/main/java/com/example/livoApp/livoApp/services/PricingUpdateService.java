package com.example.livoApp.livoApp.services;

import com.example.livoApp.livoApp.entity.Hotel;
import com.example.livoApp.livoApp.entity.HotelMinPrice;
import com.example.livoApp.livoApp.entity.Inventory;
import com.example.livoApp.livoApp.repository.HotelMinPriceRepo;
import com.example.livoApp.livoApp.repository.HotelRepo;
import com.example.livoApp.livoApp.repository.InvetoryRepo;
import com.example.livoApp.livoApp.strategy.PricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PricingUpdateService {
    private final HotelRepo hotelRepo;
    private final InvetoryRepo invetoryRepo;
    private final HotelMinPriceRepo hotelMinPriceRepo;
    private final PricingService pricingService;
    //scheduler to update the inventory and hotelMin price tables in every hour

    @Scheduled(cron = "0 0 * * * *")
    public void upadatePrice(){
        int page = 0;
        int batchSize = 100;

        while(true){
            Page<Hotel> hotelPage = hotelRepo.findAll(PageRequest.of(page , batchSize));
            if(hotelPage.isEmpty()){
                break;
            }
            hotelPage.getContent().forEach(this::upadateHotelPrice);
            page++;
        }

    }

    private void upadateHotelPrice(Hotel hotel){
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusYears(1);

        List<Inventory> inventoryList = invetoryRepo.findByHotelAndDateBetween( hotel , startDate , endDate);
        updateInventoryPrice(inventoryList);
        updateHotelMinPrice(hotel , inventoryList , startDate , endDate);
    }

    private void updateHotelMinPrice(Hotel hotel, List<Inventory> inventoryList, LocalDate startDate, LocalDate endDate) {
        Map<LocalDate , BigDecimal> dailyMinPrices = inventoryList.stream()
                .collect(Collectors.groupingBy(
                        Inventory::getDate,
                        Collectors.mapping(Inventory::getPrice , Collectors.minBy(Comparator.naturalOrder()))
                )).entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey , e -> e.getValue().orElse(BigDecimal.ZERO)));

        List<HotelMinPrice> hotelPrices = new ArrayList<>();
        dailyMinPrices.forEach((date , price)->{
            HotelMinPrice hotelPrice = hotelMinPriceRepo.findByHotelAndDate(hotel , date)
                    .orElse(new HotelMinPrice(hotel , date));
           hotelPrice.setPrice(price);
           hotelPrices.add(hotelPrice);

        });

        hotelMinPriceRepo.saveAll(hotelPrices);
    }

    private void updateInventoryPrice(List<Inventory> inventoryList){
       inventoryList.forEach(inventory -> {
     BigDecimal dynamicPrice = pricingService.calculateDynamicPrice(inventory);
     inventory.setPrice(dynamicPrice);
               });
       invetoryRepo.saveAll(inventoryList);
    }
}
