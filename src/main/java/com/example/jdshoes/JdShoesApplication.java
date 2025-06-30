package com.example.jdshoes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JdShoesApplication {

    public static void main(String[] args) {
        SpringApplication.run(JdShoesApplication.class, args);
    }

}
