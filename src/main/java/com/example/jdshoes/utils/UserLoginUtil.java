package com.example.jdshoes.utils;


import com.example.jdshoes.entity.Account;
import com.example.jdshoes.repository.AccountRepository;
import com.example.jdshoes.sercurity.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserLoginUtil {

    @Autowired
    private AccountRepository accountRepository;



    public Account getCurrentLogin(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            System.out.println("username: "+username);
            if(username.equalsIgnoreCase("anonymousUser")){
                return null;
            }
            Account user = accountRepository.findByEmail(username);
            return user;
        }
        return null;
    }

}
