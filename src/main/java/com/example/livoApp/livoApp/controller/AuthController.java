package com.example.livoApp.livoApp.controller;


import com.example.livoApp.livoApp.dto.LoginDto;
import com.example.livoApp.livoApp.dto.LoginResponseDto;
import com.example.livoApp.livoApp.dto.SignUpRequestDto;
import com.example.livoApp.livoApp.dto.UserDto;
import com.example.livoApp.livoApp.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signUp(@RequestBody SignUpRequestDto sign){
        return new ResponseEntity<>(authService.signUp(sign) , HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto , HttpServletRequest request, HttpServletResponse response){
        String token[] = authService.login(loginDto);

        Cookie cookie = new Cookie("refreshToken" , token[1] );
        cookie.setHttpOnly(true);

        response.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(token[0]));

    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> signUp(HttpServletRequest request){
        String refreshToken = Arrays.stream(request.getCookies()).
                filter(cookie -> "refreshToken".equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(()-> new AuthenticationServiceException("refresh token is not found in the Cookies"));

        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));

    }

}
