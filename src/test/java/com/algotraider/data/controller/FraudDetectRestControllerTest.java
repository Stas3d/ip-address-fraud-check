package com.algotraider.data.controller;

import com.algotraider.data.entity.*;
import com.algotraider.data.repo.*;
import com.algotraider.data.service.FraudDetectService;

import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import org.mockito.Mock;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static com.algotraider.data.util.TestData.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.MOCK;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = MOCK)
class FraudDetectRestControllerTest {

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
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new FraudDetectRestController(service))
                .build();
    }

    @Test
    @SneakyThrows
    void getUserBannedStatusControllerTest() {

        var requestDto = createUserCheckRequestDto(EMAIL);

        mockMvc.perform(MockMvcRequestBuilders.get(USER_BANNED_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value(TEST_SOURCE))
                .andExpect(jsonPath("$.userEmail").value(EMAIL))
                .andExpect(jsonPath("$.userStatus").value(false))
                .andExpect(jsonPath("$.timeStampMillis").exists());
    }

    @Test
    @SneakyThrows
    void getUserBannedStatusControllerNegativeTest() {

        var requestDto = createUserCheckRequestDto("INVALID");

        mockMvc.perform(MockMvcRequestBuilders.get(USER_BANNED_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertEquals("Invalid Email : INVALID",
                                result.getResolvedException().getMessage()));
    }

    @Test
    @SneakyThrows
    void getIpBannedStatusControllerTest() {

        var requestDto = createAddressCheckRequestDto(TEST_IP);

        mockMvc.perform(MockMvcRequestBuilders.get(IP_BANNED_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value(TEST_SOURCE))
                .andExpect(jsonPath("$.address").value(TEST_IP))
                .andExpect(jsonPath("$.ipBannedStatus").value(false))
                .andExpect(jsonPath("$.timeStampMillis").exists());
    }

    @Test
    @SneakyThrows
    void getIpBannedStatusControllerNegativeTest() {

        var requestDto = createAddressCheckRequestDto("999.9.999");

        mockMvc.perform(MockMvcRequestBuilders.get(IP_BANNED_STATUS_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertEquals("Invalid IP Address name : 999.9.999",
                                result.getResolvedException().getMessage()));
    }

    @Test
    @SneakyThrows
    void updateIpBannedStatusControllerTest() {

        when(addressRepository.save(any())).thenReturn(mockedAddress);
        when(mockedAddress.getIp()).thenReturn(TEST_IP);

        var requestDto = createUpdateIpStatusRequestDto(TEST_IP);

        mockMvc.perform(MockMvcRequestBuilders.post("/fraud-check/update-ip-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.source").value(TEST_SOURCE))
                .andExpect(jsonPath("$.address").value(TEST_IP))
                .andExpect(jsonPath("$.updatedStatus").value(false))
                .andExpect(jsonPath("$.timeStampMillis").exists());
    }

    @Test
    @SneakyThrows
    void updateIpBannedStatusWhenInternalDbErrorControllerTest() {

        var requestDto = createUpdateIpStatusRequestDto(TEST_IP);

        mockMvc.perform(MockMvcRequestBuilders.post("/fraud-check/update-ip-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().is5xxServerError())
                .andExpect(result ->
                        Assertions.assertEquals("Internal Db error",
                                result.getResolvedException().getMessage()));
    }

    @Test
    @SneakyThrows
    void updateIpBannedStatusControllerNegativeTest() {

        var requestDto = createUpdateIpStatusRequestDto("999.9.999");

        mockMvc.perform(MockMvcRequestBuilders.post("/fraud-check/update-ip-status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(requestDto)))
                .andExpect(status().isBadRequest())
                .andExpect(result ->
                        Assertions.assertEquals("Invalid IP Address name : 999.9.999",
                                result.getResolvedException().getMessage()));
    }

    @Test
    @SneakyThrows
    void processLoginControllerTest() {

        when(addressRepository.findOneByIp(any())).thenReturn(Optional.of(mockedAddress));
        when(mockedAddress.getIp()).thenReturn(TEST_IP);
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

    @Test
    @SneakyThrows
    void processLoginNegativeControllerTest() {

        mockMvc.perform(MockMvcRequestBuilders.get(CHECK_LOGIN_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(createLoginFormRequestDto())))
                .andExpect(status().isForbidden());
    }
}
