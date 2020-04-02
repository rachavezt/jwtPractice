package com.example.jwt.service.impl;

import com.example.jwt.dao.RoleDao;
import com.example.jwt.dao.UserDao;
import com.example.jwt.dto.SignUpRequestDto;
import com.example.jwt.entity.Role;
import com.example.jwt.entity.User;
import com.example.jwt.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleDao roleDao;

    @Override
    public User addUser(SignUpRequestDto signUpRequestDto) {

        Set<Role> roles = new HashSet<>();
        roles.add(roleDao.findByName(signUpRequestDto.getRoleName()));

        User user = new User();
        user.setUsername(signUpRequestDto.getUsername());
        user.setEmail(signUpRequestDto.getEmail());
        user.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        user.setRoles(roles);

        return userDao.save(user);
    }

    @Override
    public Boolean exists(String username, String email) {
        return userDao.existsByUsernameOrEmail(username, email);
    }
}
