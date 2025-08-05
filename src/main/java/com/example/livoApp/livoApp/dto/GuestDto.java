package com.example.livoApp.livoApp.dto;

import com.example.livoApp.livoApp.entity.User;
import com.example.livoApp.livoApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class GuestDto {
    private Long id;
    private String name;
    private Gender gender;
    private Integer age;
}
