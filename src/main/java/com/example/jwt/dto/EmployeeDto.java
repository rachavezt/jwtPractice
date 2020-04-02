package com.example.jwt.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class EmployeeDto {
    private Integer id;
    private String name;
    private Date dateOfBirth;
}
