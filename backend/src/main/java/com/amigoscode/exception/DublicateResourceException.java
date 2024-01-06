package com.amigoscode.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.CONFLICT)
public class DublicateResourceException extends RuntimeException {
    public DublicateResourceException(String message) {
        super(message);
    }
}
