package com.example.jdshoes.config;

import com.example.jdshoes.entity.Account;
import com.example.jdshoes.exception.UserNotActivatedException;
import com.example.jdshoes.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    @Autowired
    private CustomLoginSuccessHandler customLoginSuccessHandler;

    @Autowired
    private AccountRepository userRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/shoping-cart/**", "/cart-status/**", "/profile")
                        .authenticated()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "EMPLOYEE")
                        .requestMatchers("/admin-only/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll()
                )
                .headers(headers -> headers
                        .frameOptions().sameOrigin()  // 👈 THÊM DÒNG NÀY
                )
                .formLogin(form -> form
                        .loginPage("/user-login")
                        .successHandler(customLoginSuccessHandler)
                        .failureUrl("/user-login?error=true&email=")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/user_logout")  // URL xử lý logout
                        .logoutSuccessUrl("/user-login")  // Chuyển hướng sau khi logout thành công
                        .invalidateHttpSession(true)  // Xóa session
                        .deleteCookies("JSESSIONID")  // Xóa cookie nếu cần
                        .permitAll()
                )
                .authenticationProvider(authenticationProvider())
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            System.out.println("username login: "+username);
            Optional<Account> user = userRepository.findByEmailOpt(username);
            if (user.isEmpty()) {
                throw new UsernameNotFoundException("User " + username + " was not found in the database");
            }
            if (user.get().isNonLocked() == false) {
                throw new UserNotActivatedException(user.get().getEmail(), user.get().getEmail());
            }
            List<String> roles = new ArrayList<>();
            roles.add(user.get().getRole().getName().toString());

            List<GrantedAuthority> grantList = new ArrayList<GrantedAuthority>();
            if (roles != null) {
                for (String role : roles) {
                    GrantedAuthority authority = new SimpleGrantedAuthority(role);
                    grantList.add(authority);
                }
            }

            String password = user.get().getPassword();
            UserDetails userDetails = (UserDetails) new User(username, //
                    password, grantList);
            return userDetails;
        };
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
