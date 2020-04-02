package com.example.jwt.service.impl;

import com.example.jwt.config.security.AuthenticatedUser;
import com.example.jwt.dao.UserDao;
import com.example.jwt.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String usernameOrEmail)
            throws UsernameNotFoundException {
        // Let people login with either username or email
        UserDetails userDetails = null;
        try {
            User user = userDao.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
            userDetails = AuthenticatedUser.create(user);
        } catch (UsernameNotFoundException e) {
            new UsernameNotFoundException("User not found with username or email : " + usernameOrEmail);
        }

        return userDetails;
    }

    // This method is used by JWTAuthenticationFilter
    @Transactional
    public UserDetails loadUserById(Integer id) {
        User user = userDao.findById(id).orElseThrow(
                () -> new UsernameNotFoundException("User not found with id : " + id)
        );

        return AuthenticatedUser.create(user);
    }
}
