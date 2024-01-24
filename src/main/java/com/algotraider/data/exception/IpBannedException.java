package com.algotraider.data.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.FORBIDDEN)
public class IpBannedException extends RuntimeException {

    public IpBannedException() {
        super();
    }
}
