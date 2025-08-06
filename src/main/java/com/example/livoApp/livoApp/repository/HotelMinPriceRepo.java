package com.example.livoApp.livoApp.repository;

import com.example.livoApp.livoApp.dto.HotelPriceDto;
import com.example.livoApp.livoApp.entity.Hotel;
import com.example.livoApp.livoApp.entity.HotelMinPrice;
import com.example.livoApp.livoApp.entity.Inventory;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.lang.ScopedValue;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HotelMinPriceRepo extends JpaRepository<HotelMinPrice , Long> {
    @Query("""
    SELECT new com.example.livoApp.livoApp.dto.HotelPriceDto(i.hotel, AVG(i.price))
    FROM HotelMinPrice i
    WHERE i.hotel.city = :city
      AND i.date BETWEEN :startDate AND :endDate
      AND i.hotel.active = true
    GROUP BY i.hotel
""")
    Page<HotelPriceDto> findHotelsWithAvailableInventory(
            @Param("city") String city,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );



    Optional<HotelMinPrice> findByHotelAndDate(Hotel hotel, LocalDate date);
}
