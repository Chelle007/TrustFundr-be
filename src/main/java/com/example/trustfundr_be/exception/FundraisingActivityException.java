package com.example.trustfundr_be.exception;

import org.springframework.http.HttpStatus;

public class FundraisingActivityException extends RuntimeException {

    private final HttpStatus status;

    public FundraisingActivityException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
