package com.algotraider.data.controller;

import com.algotraider.data.dto.request.AddressCheckRequestDto;
import com.algotraider.data.dto.request.LoginFormRequestDto;
import com.algotraider.data.dto.request.UpdateIpStatusRequestDto;
import com.algotraider.data.dto.request.UserCheckRequestDto;
import com.algotraider.data.entity.Address;
import com.algotraider.data.entity.User;
import com.algotraider.data.repo.AddressRepository;
import com.algotraider.data.repo.UserRepository;
import com.algotraider.data.service.FraudDetectService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static com.algotraider.data.util.TestData.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = MOCK)
public class FraudDetectRestControllerTest {

    private MockMvc mockMvc;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;
    @Mock
    private Address mockedAddress;
    @Mock
    private User mockedUser;

    @BeforeEach
    void setup() {
        var service = new FraudDetectService(userRepository, addressRepository);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new FraudDetectRestController(service)).build();
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void getUserBannedStatusControllerTest() {

        var dto = UserCheckRequestDto.builder()
                .userEmail(EMAIL)
                .source(TEST_SOURCE)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(USER_BANNED_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value(TEST_SOURCE))
                .andExpect(jsonPath("$.userEmail").value(EMAIL))
                .andExpect(jsonPath("$.userStatus").value(false))
                .andExpect(jsonPath("$.timeStampMillis").exists());
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void getUserBannedStatusControllerNegativeTest() {

        var dto = UserCheckRequestDto.builder()
                .userEmail("INVALID")
                .source(TEST_SOURCE)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(USER_BANNED_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertEquals("Invalid Email : INVALID",
                                result.getResolvedException().getMessage()));
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void getIpBannedStatusControllerTest() {

        var dto = AddressCheckRequestDto.builder()
                .address(TEST_IP)
                .source(TEST_SOURCE)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(IP_BANNED_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value(TEST_SOURCE))
                .andExpect(jsonPath("$.address").value(TEST_IP))
                .andExpect(jsonPath("$.ipBannedStatus").value(false))
                .andExpect(jsonPath("$.timeStampMillis").exists());
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void getIpBannedStatusControllerNegativeTest() {

        var dto = AddressCheckRequestDto.builder()
                .address("999.9.999")
                .source(TEST_SOURCE)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get(IP_BANNED_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertEquals("Invalid IP Address name : 999.9.999",
                                result.getResolvedException().getMessage()));
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void updateIpBannedStatusControllerTest() {

        when(addressRepository.save(any())).thenReturn(mockedAddress);
        when(mockedAddress.getIp()).thenReturn("10.10.10.10");

        var dto = UpdateIpStatusRequestDto.builder()
                .address("10.10.10.10")
                .source(TEST_SOURCE)
                .status(Boolean.FALSE)
                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/fraud-check/update-ip-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value(TEST_SOURCE))
                .andExpect(jsonPath("$.address").value(TEST_IP))
                .andExpect(jsonPath("$.updatedStatus").value(false))
                .andExpect(jsonPath("$.timeStampMillis").exists());
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void updateIpBannedStatusWhenInternalDbErrorControllerTest() {

        var dto = createUpdateIpStatusRequestDto("10.10.10.10");

        mockMvc.perform(MockMvcRequestBuilders.post("/fraud-check/update-ip-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().is5xxServerError())
                .andExpect(result ->
                        Assertions.assertEquals("Internal Db error",
                                result.getResolvedException().getMessage()));
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void updateIpBannedStatusControllerNegativeTest() {

        var dto = createUpdateIpStatusRequestDto("999.9.999");
//                .source(TEST_SOURCE)
//                .status(Boolean.FALSE)
//                .build();

        mockMvc.perform(MockMvcRequestBuilders.post("/fraud-check/update-ip-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertEquals("Invalid IP Address name : 999.9.999",
                                result.getResolvedException().getMessage()));
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void loginControllerTest() {

        when(addressRepository.findOneByIp(any())).thenReturn(Optional.of(mockedAddress));
        when(mockedAddress.getIp()).thenReturn("10.10.10.10");
        when(mockedAddress.isBanned()).thenReturn(Boolean.FALSE);
        when(userRepository.findByEmail(any())).thenReturn(mockedUser);
        doNothing().when(mockedUser).associateNewAddress(any());

        var requestDto = createLoginFormRequestDto();

        mockMvc.perform(MockMvcRequestBuilders.get(CHECK_LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value(TEST_IP))
                .andExpect(jsonPath("$.status").value(false))
                .andExpect(jsonPath("$.userEmail").value(EMAIL))
                .andExpect(jsonPath("$.info").value(TEST_INFO))
                .andExpect(jsonPath("$.address").value(TEST_IP))
                .andExpect(jsonPath("$.proxyAddress").value(""))
                .andExpect(jsonPath("$.region").value(UA))
                .andExpect(jsonPath("$.country").value(COUNTRY))
                .andExpect(jsonPath("$.geo").value(GEO));
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void processLoginNegativeControllerTest() {

        var requestDto = createLoginFormRequestDto();

        mockMvc.perform(MockMvcRequestBuilders.get(CHECK_LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isForbidden());
    }

    private static String asJsonString(final Object obj) {

        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
