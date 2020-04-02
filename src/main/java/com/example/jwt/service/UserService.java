package com.example.jwt.service;

import com.example.jwt.dto.UserDto;
import com.example.jwt.entity.User;

public interface UserService {
    User addUser(UserDto userDto);
    User getUser(String username);
}
