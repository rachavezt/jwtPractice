package com.example.jwt.service;

import com.example.jwt.config.security.AuthenticatedUser;
import com.example.jwt.dto.SignUpRequestDto;
import com.example.jwt.entity.User;

public interface UserService {
    User addUser(SignUpRequestDto signUpRequestDto);
    Boolean exists(String username, String email);
    String samlResponse(AuthenticatedUser authenticatedUser) throws Exception;
}
