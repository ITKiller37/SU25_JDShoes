package com.example.jdshoes.service;


import com.example.jdshoes.entity.Account;
import com.example.jdshoes.entity.VerificationCode;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface VerificationCodeService {
    VerificationCode createVerificationCode(String email) throws MessagingException;

    Account verifyCode(String code);
}