package com.ecom.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ecom.model.User;



@Repository
public interface UserRepository extends JpaRepository<User, Integer>{
    
    public User findByUserEmail(String userEmail);

    public List<User> findAllByUserRole(String userRole);
}
