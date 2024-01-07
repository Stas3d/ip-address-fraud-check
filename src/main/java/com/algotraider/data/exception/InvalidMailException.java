package com.algotraider.data.exception;

public class InvalidMailException extends RuntimeException {

    public InvalidMailException() {
        super("Invalid Email");
    }

    public InvalidMailException(final String address) {
        super("Invalid Email : " + address);
    }
}
