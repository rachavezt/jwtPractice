package com.example.jwt.service.impl;

import com.example.jwt.dao.UserDao;
import com.example.jwt.dto.UserDto;
import com.example.jwt.entity.Employee;
import com.example.jwt.entity.User;
import com.example.jwt.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;

    @Override
    public User addUser(UserDto userDto) {
        User user = new User();
        BeanUtils.copyProperties(userDto, user);
        return userDao.save(user);
    }

    @Override
    public User getUser(String username) {
        return userDao.findByUsername(username);
    }
}
