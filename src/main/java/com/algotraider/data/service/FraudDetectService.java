package com.algotraider.data.service;

import com.algotraider.data.dto.request.AddressCheckRequestDto;
import com.algotraider.data.dto.request.UserCheckRequestDto;
import com.algotraider.data.dto.request.LoginFormRequestDto;
import com.algotraider.data.dto.request.UpdateIpStatusRequestDto;
import com.algotraider.data.entity.Address;
import com.algotraider.data.exception.IpBannedException;
import com.algotraider.data.exception.InvalidMailException;
import com.algotraider.data.exception.InvalidIpAddressException;
import com.algotraider.data.exception.UserCheckNotAllowedException;
import com.algotraider.data.repo.UserRepository;
import com.algotraider.data.repo.AddressRepository;
import com.algotraider.data.validation.ValidationService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FraudDetectService {

    private static final String NODE_CREATED = "FraudDetectService : New Address Node Created";

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

    public String updateIpAddressStatus(final @NonNull UpdateIpStatusRequestDto dto) {

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

    public boolean processLogin(final @NonNull LoginFormRequestDto dto) {

        checkIfUserExistsAndNotBlocked(dto.getUserEmail());
        validateIpNotBlocked(dto.getAddress());
        validateUserLinkedIpsNotBanned(dto);
        saveNewIpUserRelationIfRequired(dto);

        return recalculateAddressStatus(dto.getAddress());
    }

    @SneakyThrows
    private void checkIfUserExistsAndNotBlocked(final String userName) {

        if (userName.isBlank()) throw new UserCheckNotAllowedException();
    }

    private void saveNewIpUserRelationIfRequired(final LoginFormRequestDto dto) {

        var mail = dto.getUserEmail();
        var ip = dto.getAddress();

        boolean requiredToLinkNewAddress = userRepository.findAddresses(mail)
                .stream()
                .filter(Objects::nonNull)
                .map(Address::getIp)
                .filter(Objects::nonNull)
                .noneMatch(a -> a.equals(ip));

        if (requiredToLinkNewAddress) {
            var dbAddress = addressRepository.findOneByIp(ip)
                    .orElse(new Address(ip, NODE_CREATED));

            var linkedUser = userRepository.findByEmail(mail);
            linkedUser.associateNewAddress(dbAddress);
            userRepository.save(linkedUser);
        }
    }

    private void validateIpNotBlocked(final String currentIp) {

        addressRepository.findOneByIp(currentIp)
                .map(Address::isBanned)
                .orElseThrow(IpBannedException::new);
    }

    //Todo
    private Boolean validateUserLinkedIpsNotBanned(final @NonNull LoginFormRequestDto dto) {

        return userRepository.findAddresses(dto.getUserEmail())
                .stream()
                .filter(Address::isBanned)
                .noneMatch(a ->
                        Objects.equals(a.getIp(), dto.getAddress()));
    }


    private boolean recalculateAddressStatus(final @NonNull String address) {

        return recalculateAddressStatus(
                addressRepository.findOneByIp(address)
                        .orElse(new Address(address, NODE_CREATED)));
    }

    private boolean recalculateAddressStatus(final @NonNull Address node) {

        var success = node.getSuccessfulAttemptsNumber();
        var failed = node.getFailedAttemptsNumber();

        if (failed == 0) return Boolean.FALSE;

        if (success < calculationThreshold || failed < calculationThreshold) return Boolean.FALSE;

        if (failedThresholdValue > success / failed) {

            node.setBanned(Boolean.TRUE);
            addressRepository.save(node);
            return Boolean.TRUE;
        }
        return Boolean.FALSE;
    }
}
