package com.example.jwt.controller;

import com.example.jwt.dto.UserDto;
import com.example.jwt.entity.User;
import com.example.jwt.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = "application/json")
    public UserDto get(@PathVariable("username") String username){
        UserDto userDto = new UserDto();
        User user = userService.getUser(username);
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST, produces = "application/json")
    public UserDto add(@RequestBody UserDto userDto){
        User user = userService.addUser(userDto);
        BeanUtils.copyProperties(user, userDto);
        return userDto;
    }
}
