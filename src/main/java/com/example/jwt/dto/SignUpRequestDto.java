package com.example.jwt.dto;

import com.example.jwt.dto.enums.RoleName;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;

@Getter
@Setter
public class SignUpRequestDto {

    private String firstName;
    private String lastName;
    private String mobile;
    @Email
    private String email;
    private String username;
    private String password;
    private RoleName roleName;
}
