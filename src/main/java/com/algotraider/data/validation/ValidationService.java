package com.algotraider.data.validation;

import lombok.experimental.UtilityClass;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.commons.validator.routines.InetAddressValidator;

@UtilityClass
public class ValidationService {

    private static final InetAddressValidator ipAddressValidator = InetAddressValidator.getInstance();
    private static final EmailValidator mailValidator = EmailValidator.getInstance();

    public static boolean validateIp(final String var) {

        return ipAddressValidator.isValid(var);
    }

    public static boolean validateEmail(final String var) {

        return mailValidator.isValid(var);
    }
}
