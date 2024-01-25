package com.algotraider.data.validation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidationServiceTest {

    @Test
    void testIpAddressValidStatus() {

        var result = ValidationService.validateIp("127.0.0.1");
        Assertions.assertTrue(result);
    }

    @Test
    void testIpAddressValidStatusNegative() {

        var result = ValidationService.validateIp("327.0.0.1");
        Assertions.assertFalse(result);
    }

    @Test
    void testIsEmailValid() {

        var result = ValidationService.validateEmail("v123@mail.com");
        Assertions.assertTrue(result);
    }

    @Test
    void testIsEmailValidNegative() {

        var result = ValidationService.validateEmail("WR0NG");
        Assertions.assertFalse(result);
    }
}
