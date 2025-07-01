package com.example.jdshoes.service;


import com.example.jdshoes.dto.MailInfo;
import jakarta.mail.MessagingException;

public interface MailerService {
    void send(MailInfo mail) throws MessagingException;

    void queue(MailInfo mail);
}
