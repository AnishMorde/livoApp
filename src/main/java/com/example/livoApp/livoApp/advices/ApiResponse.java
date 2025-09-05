
package com.example.livoApp.livoApp.advices;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
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


    public ApiResponse(String message, String status) {
        this.localDateTime = LocalDateTime.now();
        this.data = (T) (message + " - " + status);
    }


}
