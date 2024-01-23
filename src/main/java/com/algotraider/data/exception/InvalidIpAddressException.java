package com.algotraider.data.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class InvalidIpAddressException extends RuntimeException {

    public InvalidIpAddressException() {
        super("Invalid IP Address name");
    }

    public InvalidIpAddressException(final String address) {
        super("Invalid IP Address name : " + address);
    }
}
