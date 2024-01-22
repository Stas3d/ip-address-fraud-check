package com.algotraider.data.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidMailException extends RuntimeException {

    public InvalidMailException() {
        super("Invalid Email");
    }

    public InvalidMailException(final String address) {
        super("Invalid Email : " + address);
    }
}
