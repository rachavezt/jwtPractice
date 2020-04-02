package com.example.jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {
    private String usernameOrEmail;
    private String password;
}
