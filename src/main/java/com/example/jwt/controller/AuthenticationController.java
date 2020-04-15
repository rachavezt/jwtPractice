package com.example.jwt.controller;

import com.example.jwt.config.security.JwtTokenProvider;
import com.example.jwt.dto.ApiResponseDto;
import com.example.jwt.dto.LoginRequestDto;
import com.example.jwt.dto.LoginResponseDto;
import com.example.jwt.dto.SignUpRequestDto;
import com.example.jwt.entity.User;
import com.example.jwt.service.UserService;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtTokenProvider tokenProvider;

    @RequestMapping(value = "/signIn", method = RequestMethod.POST, produces = "application/json")
    public LoginResponseDto authenticateUser(@Valid @RequestBody LoginRequestDto loginRequest) {

        System.out.println("Reached");

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsernameOrEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = tokenProvider.generateToken(authentication);

        LoginResponseDto loginResponseDto =  new LoginResponseDto();
        loginResponseDto.setToken("Bearer " + jwt);

        return loginResponseDto;
    }

    @RequestMapping(value = "/signUp", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequestDto signUpRequest) {
        if(userService.exists(signUpRequest.getUsername(), signUpRequest.getEmail())) {
            return new ResponseEntity(new ApiResponseDto(false, "Username or email are already taken!"),
                    HttpStatus.BAD_REQUEST);
        }

        User result = userService.addUser(signUpRequest);

        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(result.getUsername()).toUri();

        return ResponseEntity.created(location).body(new ApiResponseDto(true, "User registered successfully"));
    }

}
