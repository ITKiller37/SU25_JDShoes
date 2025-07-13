package com.example.jdshoes.config;
import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.enumClass.RoleName;
import com.example.jdshoes.repository.AccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component

public class CustomLoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private AccountRepository userRepository;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String email = authentication.getName();

        Optional<Account> user = userRepository.findByEmailOpt(email);
        if(user.get().getRole().getName().equals(RoleName.ROLE_USER)){
            response.sendRedirect("/");
        }
        if(user.get().getRole().getName().equals(RoleName.ROLE_ADMIN)){
            response.sendRedirect("/admin");
        }
        if(user.get().getRole().getName().equals(RoleName.ROLE_EMPLOYEE)){
            response.sendRedirect("/admin");
        }
    }
}

