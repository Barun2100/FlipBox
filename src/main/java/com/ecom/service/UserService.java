package com.ecom.service;

import org.springframework.stereotype.Service;

import com.ecom.model.User;

@Service
public interface UserService {

    public User saveUser(User user);

    public User getUserByEmail(String email);
    
}
