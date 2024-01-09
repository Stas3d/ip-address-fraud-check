package com.algotraider.data.service;

import com.algotraider.data.dto.AddressCheckRequestDto;
import com.algotraider.data.dto.UserCheckRequestDto;
import com.algotraider.data.dto.LoginFormDto;
import com.algotraider.data.dto.UpdateIpStatusDto;
import com.algotraider.data.entity.Address;
import com.algotraider.data.exception.*;
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

    private static final String NODE_CREATED = "New Address Node Created";

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Value("${failed.address.threshold.value:10}")
    private Long failedThresholdValue;

    @Value("${calculation.threshold.value:5}")
    private Long calculationThreshold;

    public boolean checkIfUserBanned(final @NonNull UserCheckRequestDto dto) {

        if (!ValidationService.validateEmail(dto.getUserEmail())) {
            throw new InvalidMailException(dto.getUserEmail());
        }

        var user = userRepository.findByEmail(dto.getUserEmail());
        return (user != null) ? user.isBanned() : Boolean.FALSE;
    }

    public boolean checkIfIpAddressBanned(final @NonNull AddressCheckRequestDto dto) {

        if (!ValidationService.validateIp(dto.getAddress())) {
            throw new InvalidIpAddressException(dto.getAddress());
        }

        return addressRepository.findOneByIp(dto.getAddress())
                .map(Address::isBanned)
                .orElse(Boolean.FALSE);
    }

    public String updateIpAddressStatus(final @NonNull UpdateIpStatusDto dto) {

        if (!ValidationService.validateIp(dto.getAddress())) {
            throw new InvalidIpAddressException(dto.getAddress());
        }

        boolean newStatus = (dto.getStatus() != null) ? dto.getStatus() : Boolean.FALSE;
        var address = addressRepository.findOneByIp(dto.getAddress())
                .orElse(new Address(dto.getAddress(), NODE_CREATED));

        address.setBanned(newStatus);

        if (Boolean.TRUE.equals(dto.getStatus())) {
            address.updateSuccessfulAttempts();
        } else {
            address.updateFailedAttempts();
        }

        return addressRepository.save(address).getIp();
    }

    public boolean processLogin(final @NonNull LoginFormDto loginDtoForm) {

        checkIfUserExistsAndNotBlocked(loginDtoForm.getUserEmail());
        validateIpNotBlocked(loginDtoForm.getAddress());
        validateUserLinkedIpsNotBanned(loginDtoForm);
        saveNewIpUserRelationIfRequired(loginDtoForm);

        recalculateAddressStatus(loginDtoForm.getAddress());
        return Boolean.TRUE;
    }

    @SneakyThrows
    private void checkIfUserExistsAndNotBlocked(final String userName) {

        if (userName.isBlank()) throw new UserCheckNotAllowedException();
    }

    private void saveNewIpUserRelationIfRequired(final LoginFormDto loginDtoForm) {

        var mail = loginDtoForm.getUserEmail();
        var ip = loginDtoForm.getAddress();
        var needToLinkAddress = userRepository.findAddresses(mail)
                .stream()
                .filter(Objects::nonNull)
                .map(Address::getIp)
                .filter(Objects::nonNull)
                .noneMatch(a -> a.equals(ip));

        if (needToLinkAddress) {
            var dbAddress = addressRepository.findOneByIp(ip)
                    .orElse(new Address(ip, NODE_CREATED));

            var linkedUser = userRepository.findByEmail(mail);
            linkedUser.associateNewAddress(dbAddress);
            userRepository.save(linkedUser);
        }
    }

    private Boolean validateIpNotBlocked(final String currentIp) {

        return addressRepository.findOneByIp(currentIp)
                .map(Address::isBanned)
                .orElseThrow(IpBannedException::new);
    }

    //Todo
    private Boolean validateUserLinkedIpsNotBanned(final @NonNull LoginFormDto loginDtoForm) {

        return userRepository.findAddresses(loginDtoForm.getUserEmail())
                .stream()
                .filter(Address::isBanned)
                .noneMatch(a ->
                        Objects.equals(a.getIp(), loginDtoForm.getAddress()));
    }


    private boolean recalculateAddressStatus(final @NonNull String address) {

        return recalculateAddressStatus(
                addressRepository.findOneByIp(address)
                        .orElse(new Address(address, NODE_CREATED)));
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
