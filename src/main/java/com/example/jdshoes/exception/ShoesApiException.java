package com.example.jdshoes.exception;

import org.springframework.http.HttpStatus;

public class ShoesApiException extends RuntimeException{
    private HttpStatus status;
    private String message;

    public ShoesApiException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    public ShoesApiException(HttpStatus status, String message, String messageError) {
        super(messageError);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
