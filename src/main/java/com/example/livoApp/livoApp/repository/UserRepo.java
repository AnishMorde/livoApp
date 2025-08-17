package com.example.livoApp.livoApp.repository;

import com.example.livoApp.livoApp.entity.User;
import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {



   Optional <User> findByEmail(String email);
}
