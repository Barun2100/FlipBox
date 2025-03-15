package com.ecom.config;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import com.ecom.model.User;
import com.ecom.repository.UserRepository;
import com.ecom.service.UserService;
import com.ecom.util.AppConstant;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthFailHandlerImpl extends SimpleUrlAuthenticationFailureHandler{

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");
        //System.out.println("Failed login with email: "+email);

        User user = userRepository.findByUserEmail(email);
        if(user.getIsEnabled()){
            if(user.getIsNonLocked()){
                if(user.getFailedAttempt()<AppConstant.ATTEMPTS_TIME){
                    userService.increseFailedAttempts(user);
                }
                else{
                    userService.userAccountLock(user);
                    exception = new LockedException("Your account is locked by System !! failed attempts 3");
                }
            }
            else{
                if(userService.unlockUserAccountTimeExpired(user)){
                    exception = new LockedException("Your account is unlocked by System !! please try to login");
                }
                else{
                    exception = new LockedException("Your account is locked by System please try after sometimes");
                }
            }
        }
        else{
            exception = new LockedException("Your account is not active");
        }
        super.setDefaultFailureUrl("/signin?error");
        super.onAuthenticationFailure(request, response, exception);
    }

    
}
