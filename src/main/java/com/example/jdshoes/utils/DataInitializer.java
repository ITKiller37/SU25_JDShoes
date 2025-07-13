package com.example.jdshoes.utils;

import com.example.jdshoes.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        String password = "123456";
        String email = "admin@gmail.com";

//        Account account = accountRepository.findByEmail(email);
//        if (account != null) {
//            account.setPassword(passwordEncoder.encode(password));
//            accountRepository.save(account);
//        }
    }
}
