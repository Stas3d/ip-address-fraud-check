package com.algotraider.data.controller;

import com.algotraider.data.dto.request.AddressCheckRequestDto;
import com.algotraider.data.dto.request.UserCheckRequestDto;
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

    @BeforeEach
    void setup() {

        var service = new FraudDetectService(userRepository, addressRepository);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new FraudDetectRestController(service)).build();
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void getUserBannedStatusControllerTest() {

        var dto = UserCheckRequestDto.builder()
                .userEmail("user@user.com")
                .source("Requested from mock test source")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/fraud-check/user-banned-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("Requested from mock test source"))
                .andExpect(jsonPath("$.userEmail").value("user@user.com"))
                .andExpect(jsonPath("$.userStatus").value(false))
                .andExpect(jsonPath("$.timeStampMillis").exists());
    }

    @org.junit.jupiter.api.Test
    @SneakyThrows
    void getUserBannedStatusControllerNegativeTest() {

        var dto = UserCheckRequestDto.builder()
                .userEmail("INVALID")
                .source("Requested from mock test source")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/fraud-check/user-banned-status")
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
                .address("10.10.10.10")
                .source("Requested from mock test source")
                .build();

        mockMvc.perform(MockMvcRequestBuilders.get("/fraud-check/ip-banned-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value("Requested from mock test source"))
                .andExpect(jsonPath("$.address").value("10.10.10.10"))
                .andExpect(jsonPath("$.ipBannedStatus").value(false))
                .andExpect(jsonPath("$.timeStampMillis").exists());
    }

    private static String asJsonString(final Object obj) {

        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
