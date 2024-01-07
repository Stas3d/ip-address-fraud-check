package com.algotraider.data.service;

import com.algotraider.data.exception.InvalidIpAddressException;
import com.algotraider.data.exception.InvalidMailException;
import com.algotraider.data.validation.ValidationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertThrows;

@SpringBootTest
public class ValidationServiceTest {

    private static final String INVALID_IP_ADDRESS_NAME = "Invalid IP Address name";
    private static final String INVALID_EMAIL = "Invalid Email";

    @Autowired
    private ValidationService service;

    @Test
    void testIpAddressValidStatus() {

        var test = service.validateIp("127.0.0.1");
        Assertions.assertTrue(test);
    }

    @Test
    void testIpAddressValidStatusNegative() {

        var exception = assertThrows(
                InvalidIpAddressException.class,
                () -> {
                    service.validateIp("327.0.0.1");
                });

        Assertions.assertTrue(exception.getMessage().contains(INVALID_IP_ADDRESS_NAME));
    }

    @Test
    void testIsEmailValid() {

        var test = service.validateEmail("v123@mail.com");
        Assertions.assertTrue(test);
    }

    @Test
    void testIsEmailValidNegative() {

        var exception = assertThrows(
                InvalidMailException.class,
                () -> {
                    service.validateEmail("WR0NG");
                });

        Assertions.assertTrue(exception.getMessage().contains(INVALID_EMAIL));
    }
}
