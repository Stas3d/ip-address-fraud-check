package com.algotraider.data.validation;

import com.algotraider.data.exception.InvalidIpAddressException;
import com.algotraider.data.exception.InvalidMailException;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    private final InetAddressValidator ipAddressValidator = InetAddressValidator.getInstance();
    private final EmailValidator mailValidator = EmailValidator.getInstance();

    public Boolean validateIp(final String var) {

        if (ipAddressValidator.isValid(var)) return Boolean.TRUE;
        else throw new InvalidIpAddressException();
    }

    public Boolean validateEmail(final String var) {

        if (mailValidator.isValid(var)) return Boolean.TRUE;
        else throw new InvalidMailException();
    }
}
