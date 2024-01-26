package com.algotraider.data.service;

import com.algotraider.data.dto.request.IpCheckRequestDto;
import com.algotraider.data.dto.request.LoginFormRequestDto;
import com.algotraider.data.dto.request.UpdateIpStatusRequestDto;
import com.algotraider.data.dto.request.UserCheckRequestDto;
import com.algotraider.data.entity.Address;
import com.algotraider.data.entity.User;
import com.algotraider.data.exception.InvalidIpAddressException;
import com.algotraider.data.exception.InvalidMailException;
import com.algotraider.data.exception.IpBannedException;
import com.algotraider.data.exception.UserNotFoundException;
import com.algotraider.data.repo.AddressRepository;
import com.algotraider.data.repo.UserRepository;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class FraudDetectServiceTest {

    private static final String MAIL_USER_COM = "mail@user.com";
    private static final String IP_ADDRESS = "23.23.23.23";
    private static final String META = "test-meta-info";
    private static final String TEST_SOURCE = "test-source";
    private static final User user = new User(IP_ADDRESS, MAIL_USER_COM, System.currentTimeMillis());
    private static final Optional<User> userOptional = Optional.of(user);
    private static final Address address = new Address(IP_ADDRESS, META);

    private FraudDetectService service;
    @Value("${failed.address.threshold.value:10}")
    private Long failedThresholdValue;
    @Value("${calculation.threshold.value:5}")
    private Long calculationThreshold;
    @Mock
    private Address mockAddress;
    @Mock
    private UserRepository userRepository;
    @Mock
    private AddressRepository addressRepository;

    @BeforeEach
    public void setup() {
        service = new FraudDetectService(userRepository, addressRepository);
    }

    @Test
    void checkIfUserBannedTest() {

        var dto = new UserCheckRequestDto(TEST_SOURCE, MAIL_USER_COM);
        var result = service.checkIfUserBanned(dto);
        Assertions.assertFalse(result);
    }

    @Test
    void checkIfUserBannedWithStatusFromDbTest() {

        when(userRepository.findByEmail(any())).thenReturn(userOptional);
//        when(userOptional.get()).thenReturn(user);
        var dto = new UserCheckRequestDto(TEST_SOURCE, MAIL_USER_COM);
        var result = service.checkIfUserBanned(dto);
        Assertions.assertFalse(result);
    }

    @Test
    void checkIfUserBannedNegativeTest() {

        var dto = new UserCheckRequestDto(TEST_SOURCE, "WR0NG@MAIL");
        var exception = assertThrows(
                InvalidMailException.class,
                () -> {
                    service.checkIfUserBanned(dto);
                });
        Assertions.assertTrue(exception.getMessage().contains("Invalid Email : WR0NG@MAIL"));
    }

    @Test
    void checkIfIpAddressBannedTest() {
        var dto = new IpCheckRequestDto(TEST_SOURCE, IP_ADDRESS);
        var result = service.checkIfIpAddressBanned(dto);
        Assertions.assertFalse(result);
    }

    @Test
    void checkIfIpAddressBannedNegativeTest() {
        var dto = new IpCheckRequestDto(TEST_SOURCE, "WR0NG");

        var exception = assertThrows(
                InvalidIpAddressException.class,
                () -> {
                    service.checkIfIpAddressBanned(dto);
                });
        Assertions.assertTrue(exception.getMessage().contains("Invalid IP Address name : WR0NG"));
    }

    @Test
    void updateIpAddressStatusTest() {

        when(addressRepository.save(any())).thenReturn(mockAddress);
        when(mockAddress.getIp()).thenReturn(IP_ADDRESS);

        UpdateIpStatusRequestDto dto = UpdateIpStatusRequestDto.builder()
                .source(TEST_SOURCE)
                .address(IP_ADDRESS)
                .status(Boolean.FALSE)
                .lastUpdated(System.currentTimeMillis())
                .build();

        var result = service.updateIpAddressStatus(dto);
        Assertions.assertEquals(IP_ADDRESS, result.getAddress());
    }

    @Test
    void updateIpAddressStatusNegativeTest() {
        UpdateIpStatusRequestDto dto = UpdateIpStatusRequestDto.builder()
                .address("WR0NG")
                .build();

        var exception = assertThrows(
                InvalidIpAddressException.class,
                () -> {
                    service.updateIpAddressStatus(dto);
                });
        Assertions.assertTrue(exception.getMessage().contains("Invalid IP Address name : WR0NG"));
    }

    @Test
    void processLoginNegativeTest() {
        when(userRepository.findByEmail(any())).thenReturn(userOptional);


        var dto = LoginFormRequestDto.builder()
                .userEmail(MAIL_USER_COM)
                .info("test-info")
                .address(IP_ADDRESS)
                .proxyAddress("")
                .region("UA")
                .country("Ukraine")
                .geo("12345678;12345678")
                .loginTime(System.currentTimeMillis())
                .build();

        address.setUser(user);
        when(userRepository.findAddresses(any())).thenReturn(List.of(address));

        assertThrows(IpBannedException.class,
                () -> {
                    service.processLogin(dto);
                });
    }

    @Test
    void processLoginNotFoundNegativeTest() {


        var dto = LoginFormRequestDto.builder()
                .userEmail(MAIL_USER_COM)
                .info("test-info")
                .address(IP_ADDRESS)
                .proxyAddress("")
                .region("UA")
                .country("Ukraine")
                .geo("12345678;12345678")
                .loginTime(System.currentTimeMillis())
                .build();

        address.setUser(user);
        when(userRepository.findAddresses(any())).thenReturn(List.of(address));

        assertThrows(UserNotFoundException.class,
                () -> {
                    service.processLogin(dto);
                });
    }

    @Test
    void processLoginTest() {

        when(userRepository.findByEmail(any())).thenReturn(userOptional);

        address.setUser(user);
        when(userRepository.findAddresses(any())).thenReturn(List.of(address));
        when(mockAddress.isBanned()).thenReturn(Boolean.FALSE);
        when(addressRepository.findOneByIp(any())).thenReturn(Optional.of(mockAddress));

        var dto = LoginFormRequestDto.builder()
                .userEmail(MAIL_USER_COM)
                .info("test-info")
                .address(IP_ADDRESS)
                .proxyAddress("")
                .region("UA")
                .country("Ukraine")
                .geo("12345678;12345678")
                .loginTime(System.currentTimeMillis())
                .build();

        boolean result = service.processLogin(dto);
        Assertions.assertFalse(result);
    }
}
