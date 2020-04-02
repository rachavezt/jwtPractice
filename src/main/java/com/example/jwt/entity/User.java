package com.example.jwt.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
@Getter
@Setter
public class User {

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String email;
}
