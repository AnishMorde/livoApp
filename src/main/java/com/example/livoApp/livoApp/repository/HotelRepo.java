package com.example.livoApp.livoApp.repository;

import com.example.livoApp.livoApp.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface HotelRepo extends JpaRepository<Hotel , Long> {
}
