package com.example.livoApp.livoApp.repository;

import com.example.livoApp.livoApp.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepo  extends JpaRepository<Room, Long> {
}
