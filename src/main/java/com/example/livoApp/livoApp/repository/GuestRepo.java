package com.example.livoApp.livoApp.repository;

import com.example.livoApp.livoApp.entity.Guest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GuestRepo extends JpaRepository<Guest , Long> {
}
