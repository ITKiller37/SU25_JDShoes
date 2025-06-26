package com.example.jdshoes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpRequestResponseHolder;
import org.springframework.security.web.context.SecurityContextRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .anyRequest().permitAll()
                .and()
                .securityContext()
                .securityContextRepository(fakeAdminContextRepository());

        return http.build();
    }

    private SecurityContextRepository fakeAdminContextRepository() {
        return new SecurityContextRepository() {
            @Override
            public SecurityContext loadContext(HttpRequestResponseHolder requestResponseHolder) {
                SecurityContext context = SecurityContextHolder.createEmptyContext();

                Authentication authentication = new UsernamePasswordAuthenticationToken(
                        "admin@example.com",
                        null,
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
                );

                context.setAuthentication(authentication);
                return context;
            }

            @Override
            public void saveContext(SecurityContext context, HttpServletRequest request, HttpServletResponse response) {
            }

            @Override
            public boolean containsContext(HttpServletRequest request) {
                return true;
            }
        };
    }
}
