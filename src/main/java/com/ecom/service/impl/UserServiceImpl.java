package com.ecom.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;



@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Override
    public User saveUser(User user) {
       User savedUser = userRepository.save(user);
       return savedUser;
    }
    
}
