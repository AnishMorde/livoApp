package com.example.livoApp.livoApp.dto;

import com.example.livoApp.livoApp.entity.Hotel;
import lombok.Data;


import java.math.BigDecimal;

@Data
public class Roomdto {
    private Long id;
    private String type;
    private BigDecimal basePrice;
    private Integer totalCount;
    private Integer capacity;
    private String[] photos;
    private String [] amenities;

}
