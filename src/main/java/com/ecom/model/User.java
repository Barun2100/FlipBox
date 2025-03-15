package com.ecom.model;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    private String userName;

    private String userEmail;

    private String userPassword;

    private String userPhone;

    private String userAddress;

    private String userCity;

    private String userState;

    private String userPIN;

    private String userImage;

    private String userRole;
    
    private Boolean isEnabled;

    private Boolean isNonLocked;;

    private Integer failedAttempt;

    private Date lockTime;
}
