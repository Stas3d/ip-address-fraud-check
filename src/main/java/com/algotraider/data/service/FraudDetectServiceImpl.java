package com.algotraider.data.service;

import com.algotraider.data.dto.AddressCheckRequestDto;
import com.algotraider.data.dto.UserCheckRequestDto;
import com.algotraider.data.dto.LoginFormDto;
import com.algotraider.data.dto.UpdateIpStatusDto;
import com.algotraider.data.entity.Address;
import com.algotraider.data.exception.InvalidIpAddressException;
import com.algotraider.data.exception.InvalidMailException;
import com.algotraider.data.exception.UserCheckNotAllowedException;
import com.algotraider.data.repo.UserRepository;
import com.algotraider.data.repo.AddressRepository;

import com.algotraider.data.validation.ValidationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FraudDetectServiceImpl implements FraudDetectService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ValidationService validation;

    @Value("${failed.address.threshold.value:10}")
    private Long failedThresholdValue;

    @Value("${calculation.threshold.value:5}")
    private Long calculationThreshold;

    public boolean checkIfUserBanned(final @NonNull UserCheckRequestDto dto) {

        if (!validation.validateEmail(dto.getUserEmail())) {
            throw new InvalidMailException(dto.getUserEmail());
        }

        var user = userRepository.findByEmail(dto.getUserEmail());
        return (user != null) ? user.isBanned() : Boolean.FALSE;
    }

    public boolean checkIfIpAddressBanned(final @NonNull AddressCheckRequestDto dto) {

        if (!validation.validateIp(dto.getAddress())) {
            throw new InvalidIpAddressException(dto.getAddress());
        }
        var address = addressRepository.findOneByIp(dto.getAddress());
        return (address != null) ? address.isBanned() : Boolean.FALSE;
    }

    public String updateIpAddressStatus(final @NonNull UpdateIpStatusDto dto) {

        if (!validation.validateIp(dto.getAddress())) {
            throw new InvalidIpAddressException(dto.getAddress());
        }
        var newStatus = dto.getStatus();
        var address = addressRepository.findOneByIp(dto.getAddress());
        address.setBanned(newStatus);

        if (Boolean.TRUE.equals(dto.getStatus())) {
            address.updateSuccessfulAttempts();
        } else {
            address.updateFailedAttempts();
        }

        return addressRepository.save(address).getIp();
    }

    public boolean processLogin(final @NonNull LoginFormDto loginDtoForm) {

        var userEmail = loginDtoForm.getUserEmail();
        var currentIp = loginDtoForm.getAddress();
        checkIfUserExistsAndNotBlocked(userEmail);
        checkIfIpNotBlocked(currentIp);

//        var firstCheck = userRepository.findAddresses(userEmail)
//                .stream()
//                .anyMatch(u ->
//                        Objects.equals(u.getIp(), currentIp));

        var firstCheck = userRepository.findAddresses(userEmail)
                .stream()
                .filter(Address::isBanned)
                .noneMatch(u ->
                        Objects.equals(u.getIp(), currentIp));

        if (firstCheck) {
            return Boolean.TRUE;
        }
        saveNewIpUserRelation(loginDtoForm);
        return Boolean.TRUE;
    }

    @SneakyThrows
    private void checkIfUserExistsAndNotBlocked(final String userName) {

        if (userName.isBlank()) throw new UserCheckNotAllowedException();
    }

    private void saveNewIpUserRelation(final LoginFormDto loginDtoForm) {

        Address address = new Address(loginDtoForm.getAddress(), "");
        var linkedUser = userRepository.findByEmail(loginDtoForm.getUserEmail());
        addressRepository.save(address);
    }

    private Boolean checkIfIpNotBlocked(final String currentIp) {

        var address = addressRepository.findOneByIp(currentIp);
        return (address != null) ? !address.isBanned() : Boolean.TRUE;
    }

    private boolean recalculateAddressStatus(final @NonNull Address address) {

        var success = address.getSuccessfulAttemptsNumber();
        var failed = address.getFailedAttemptsNumber();

        if (failed == 0) return Boolean.FALSE;

        if (success < calculationThreshold || failed < calculationThreshold) return Boolean.FALSE;

        if (failedThresholdValue > success / failed) {

            address.setBanned(Boolean.TRUE);
            addressRepository.save(address);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
