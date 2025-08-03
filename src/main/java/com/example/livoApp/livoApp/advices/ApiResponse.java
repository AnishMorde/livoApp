
package com.example.livoApp.livoApp.advices;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiResponse<T> {

    @JsonFormat(pattern = "hh:mm:ss dd-MM-yyyy")
    private LocalDateTime localDateTime;
    private T data;
    private  com.example.livoApp.livoApp.advices.ApiError error;

    public ApiResponse(){
        this.localDateTime = LocalDateTime.now();
    }

    public ApiResponse(T data){
        this();
        this.data = data;
    }

    public ApiResponse( com.example.livoApp.livoApp.advices.ApiError error) {
        this();
        this.error = error;
    }
}
