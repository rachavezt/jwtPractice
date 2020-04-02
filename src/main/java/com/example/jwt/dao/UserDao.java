package com.example.jwt.dao;

import com.example.jwt.entity.User;
import org.springframework.data.repository.CrudRepository;

public interface UserDao extends CrudRepository<User, Integer> {
    User findByUsername(String username);
}
