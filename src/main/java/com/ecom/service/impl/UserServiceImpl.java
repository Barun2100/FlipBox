package com.ecom.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;



@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User saveUser(User user) {
        user.setUserRole("ROLE_USER");
        user.setIsEnabled(true);
        user.setIsNonLocked(true);
        user.setFailedAttempt(0);       
        user.setLockTime(null);
        String encodePassword = passwordEncoder.encode(user.getUserPassword());
        user.setUserPassword(encodePassword);
        User savedUser = userRepository.save(user);
        return savedUser;
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    @Override
    public List<User> getAllUsers(String userRole) {
        return userRepository.findAllByUserRole(userRole);
    }

    @Override
    public boolean updateUserStatus(Boolean status, int id) {
        Optional<User> user = userRepository.findById(id);
        if(user.isPresent()) {
            user.get().setIsEnabled(status);
            userRepository.save(user.get());
            return true;
        }
        return false;
    }

    @Override
    public void increseFailedAttempts(User user) {
        int attempts=user.getFailedAttempt()+1;
        user.setFailedAttempt(attempts);
        userRepository.save(user);

    }

    @Override
    public void userAccountLock(User user) {
        user.setIsNonLocked(false);
        user.setLockTime(new Date());
        userRepository.save(user);
    }

    @Override
    public boolean unlockUserAccountTimeExpired(User user) {
        long lockTime = user.getLockTime().getTime();
        long unlockTime = lockTime + AppConstant.UNLOCK_DURATION_TIME;
        long currentTime = System.currentTimeMillis();
        if(unlockTime<currentTime) {
            user.setIsNonLocked(true);
            user.setLockTime(null);
            user.setFailedAttempt(0);
            userRepository.save(user);
            return true;
        }
        return false;
    }
    
}
