package com.algotraider.data.util;

import com.algotraider.data.dto.request.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TestData {

    public static final String META = "test-meta-info";
    public static final String TEST_SOURCE = "Requested from mock test source";
    public static final String TEST_IP = "10.10.10.10";
    public static final String CHECK_LOGIN_ENDPOINT = "/fraud-check/login";
    public static final String IP_BANNED_STATUS_ENDPOINT = "/fraud-check/ip-banned-status";
    public static final String USER_BANNED_STATUS_ENDPOINT = "/fraud-check/user-banned-status";
    public static final String EMAIL = "user@user.com";
    public static final String TEST_INFO = "test-info";
    public static final String UA = "UA";
    public static final String COUNTRY = "Ukraine";
    public static final String GEO = "12345678;12345678";

    public static StatRequestDto createLinkedIpStatFormRequestDto(final String mail) {

        return StatRequestDto.builder()
                .userEmail(mail)
                .source(TEST_SOURCE)
                .build();
    }

    public static LoginFormRequestDto createLoginFormRequestDto() {

        return LoginFormRequestDto.builder()
                .userEmail(EMAIL)
                .info(TEST_INFO)
                .address(TEST_IP)
                .proxyAddress("")
                .region(UA)
                .country(COUNTRY)
                .geo(GEO)
                .loginTime(System.currentTimeMillis())
                .build();
    }

    public static UpdateIpStatusRequestDto createUpdateIpStatusRequestDto(final String ip) {

        return UpdateIpStatusRequestDto.builder()
                .address(ip)
                .source(TEST_SOURCE)
                .status(Boolean.FALSE)
                .build();
    }

    public static UserCheckRequestDto createUserCheckRequestDto(final String mail) {

        return UserCheckRequestDto.builder()
                .userEmail(mail)
                .source(TEST_SOURCE)
                .build();
    }

    public static IpCheckRequestDto createAddressCheckRequestDto(final String ip) {

        return IpCheckRequestDto.builder()
                .address(ip)
                .source(TEST_SOURCE)
                .build();
    }


    public static String asJsonString(final Object obj) {

        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
