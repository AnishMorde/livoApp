package com.example.livoApp.livoApp.dto;

import com.example.livoApp.livoApp.entity.Guest;
import com.example.livoApp.livoApp.entity.Hotel;
import com.example.livoApp.livoApp.entity.Room;
import com.example.livoApp.livoApp.entity.User;
import com.example.livoApp.livoApp.entity.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class BookingDto {
    private Long id;
    private Integer roomsCount;
    private LocalDateTime checkInDate;
    private LocalDateTime checkOutDate;
    private LocalDateTime createdAt;
    private BookingStatus bookStatus;
    private Set<GuestDto> guests;
}
