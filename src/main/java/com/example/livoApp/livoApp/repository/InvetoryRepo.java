package com.example.livoApp.livoApp.repository;

import com.example.livoApp.livoApp.entity.Hotel;
import com.example.livoApp.livoApp.entity.Inventory;
import com.example.livoApp.livoApp.entity.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface InvetoryRepo extends JpaRepository<Inventory, Long> {
      void deleteByRoom(Room room);

      @Query("""
        SELECT DISTINCT i.hotel
        FROM Inventory i
        WHERE i.city = :city
          AND i.date BETWEEN :startDate AND :endDate
          AND i.closed = false
          AND NOT EXISTS (
              SELECT 1
              FROM Inventory i2
              WHERE i2.hotel = i.hotel
                AND i2.date BETWEEN :startDate AND :endDate
                AND i2.closed = false
                AND (i2.totalCount - i2.bookedCount) < :roomsCount
          )
        GROUP BY i.hotel
        HAVING COUNT(DISTINCT i.date) = :dateCount
    """)
      Page<Hotel> findHotelsWithAvailableInventory(
              @Param("city") String city,
              @Param("startDate") LocalDate startDate,
              @Param("endDate") LocalDate endDate,
              @Param("roomsCount") Integer roomsCount,
              @Param("dateCount") long dateCount,
              Pageable pageable
      );

      @Query("""
        SELECT i
        FROM Inventory i
        WHERE i.room.id = :roomId
            AND i.date BETWEEN :startDate AND :endDate
            AND i.closed = false
            AND (i.totalCount - i.bookedCount - i.reservedCount) >= :roomsCount
    """)
      @Lock(LockModeType.PESSIMISTIC_WRITE)
      List<Inventory> findAndAvailableInventory(
              @Param("roomId") Long roomId,
              @Param("startDate") LocalDate startDate,
              @Param("endDate") LocalDate endDate,
              @Param("roomsCount") Integer roomsCount
      );


      List<Inventory> findByHotelAndDateBetween(Hotel hotel, LocalDate startDate, LocalDate endDate);
}