package com.algotraider.data.exception;

public class InvalidIpAddressException extends RuntimeException {

    public InvalidIpAddressException() {
        super("Invalid IP Address name");
    }

    public InvalidIpAddressException(final String address) {
        super("Invalid IP Address name : " + address);
    }
}
