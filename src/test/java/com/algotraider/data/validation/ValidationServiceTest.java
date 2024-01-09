package com.algotraider.data.validation;

import com.algotraider.data.exception.InvalidIpAddressException;
import com.algotraider.data.exception.InvalidMailException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertThrows;

public class ValidationServiceTest {

    private static final String INVALID_IP_ADDRESS_NAME = "Invalid IP Address name";
    private static final String INVALID_EMAIL = "Invalid Email";

    @Test
    void testIpAddressValidStatus() {

        var result = ValidationService.validateIp("127.0.0.1");
        Assertions.assertTrue(result);
    }

    @Test
    @Disabled
    void testIpAddressValidStatusNegative() {

        var exception = assertThrows(
                InvalidIpAddressException.class,
                () -> {
                    ValidationService.validateIp("327.0.0.1");
                });

        Assertions.assertTrue(exception.getMessage().contains(INVALID_IP_ADDRESS_NAME));
    }

    @Test
    void testIsEmailValid() {

        var result = ValidationService.validateEmail("v123@mail.com");
        Assertions.assertTrue(result);
    }

    @Test
    @Disabled
    void testIsEmailValidNegative() {

        var exception = assertThrows(
                InvalidMailException.class,
                () -> {
                    ValidationService.validateEmail("WR0NG");
                });

        Assertions.assertTrue(exception.getMessage().contains(INVALID_EMAIL));
    }
}
