package com.example.livoApp.livoApp.security;


import com.example.livoApp.livoApp.dto.LoginDto;
import com.example.livoApp.livoApp.dto.SignUpRequestDto;
import com.example.livoApp.livoApp.dto.UserDto;
import com.example.livoApp.livoApp.entity.User;
import com.example.livoApp.livoApp.entity.enums.Role;
import com.example.livoApp.livoApp.exception.ResourceNotFoundException;
import com.example.livoApp.livoApp.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepo userRepo;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDto signUp(SignUpRequestDto sign){
        User user = userRepo.findByEmail(sign.getEmail()).orElse(null);

        if(user != null){
            throw new RuntimeException("User already with this email id ");
        }


        User newUser = modelMapper.map(sign , User.class);
        newUser.setRoles(Set.of(Role.GUEST));
        newUser.setPassword(passwordEncoder.encode(sign.getPassword()));
        newUser = userRepo.save(newUser);

        return modelMapper.map(newUser , UserDto.class);

    }

    public String[] login(LoginDto loginDto){
        System.out.println("Start login");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(), loginDto.getPassword()
                )
        );

        System.out.println("Authentication complete");

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = (User) authentication.getPrincipal();

        String arr[] = new String[2];
        arr[0] = jwtService.generateToken(user);
        arr[1] = jwtService.generateRefreshToken(user);

        return arr;
    }

    public String refreshToken(String refreshToken){
        Long id = jwtService.getUserIdFromToken(refreshToken);

        User user = userRepo.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("User not found "));
        return jwtService.generateToken(user);


    }
}
