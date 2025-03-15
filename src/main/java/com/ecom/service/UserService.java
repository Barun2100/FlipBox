package com.ecom.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ecom.model.User;

@Service
public interface UserService {

    public User saveUser(User user);

    public User getUserByEmail(String email);

    public List<User> getAllUsers(String userRole);

    public boolean updateUserStatus(Boolean status, int id);

    public void increseFailedAttempts(User user);

    public void userAccountLock(User user);

    public boolean unlockUserAccountTimeExpired(User user);

    
}
