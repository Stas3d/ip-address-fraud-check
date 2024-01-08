package com.algotraider.data.validation;

import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    private final InetAddressValidator ipAddressValidator = InetAddressValidator.getInstance();
    private final EmailValidator mailValidator = EmailValidator.getInstance();

    public boolean validateIp(final String var) {

        return ipAddressValidator.isValid(var);
    }

    public boolean validateEmail(final String var) {

        return mailValidator.isValid(var);
    }
}
