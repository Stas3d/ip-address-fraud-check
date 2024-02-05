package com.algotraider.data.service;

import com.algotraider.data.dto.request.IpCheckRequestDto;
import com.algotraider.data.dto.request.LoginFormRequestDto;
import com.algotraider.data.dto.request.UpdateIpStatusRequestDto;
import com.algotraider.data.dto.response.UpdateIpStatusResponseDto;
import com.algotraider.data.entity.Address;
import com.algotraider.data.entity.User;
import com.algotraider.data.exception.*;
import com.algotraider.data.repo.UserRepository;
import com.algotraider.data.repo.AddressRepository;
import com.algotraider.data.validation.ValidationService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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

    public boolean checkIfUserBanned(final @NonNull String mail) {

        if (!ValidationService.validateEmail(mail)) {
            throw new InvalidMailException(mail);
        }
        var user = userRepository.findByEmail(mail);
        return user.map(User::isBanned).orElse(Boolean.FALSE);
    }

    public boolean checkIfIpAddressBanned(final @NonNull IpCheckRequestDto dto) {

        if (!ValidationService.validateIp(dto.getAddress())) {
            throw new InvalidIpAddressException(dto.getAddress());
        }

        return addressRepository.findOneByIp(dto.getAddress())
                .map(Address::isBanned)
                .orElse(Boolean.FALSE);
    }

    public UpdateIpStatusResponseDto updateIpAddressStatus(final @NonNull UpdateIpStatusRequestDto dto) {

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

        String ip;
        try {
            ip = addressRepository.save(address).getIp();
        } catch (Exception ex) {
            throw new InternalDbException();
        }
        return UpdateIpStatusResponseDto.builder()
                .source(dto.getSource())
                .address(ip)
                .timeStampMillis(Instant.now().toEpochMilli())
                .updatedStatus(dto.getStatus())
                .build();
    }

    public boolean processLogin(final @NonNull LoginFormRequestDto dto) {

        checkIfUserExistsAndNotBlocked(dto.getUserEmail());
        validateIpNotBlocked(dto.getAddress());
        validateUserLinkedIpsNotBanned(dto);
        saveNewIpUserRelationIfRequired(dto);

        return recalculateAddressStatus(dto.getAddress());
    }

    public List<String> linkedIpsStatForUser(@NonNull final String email) {

        checkIfUserExistsAndNotBlocked(email);

        return userRepository.findAddresses(email)
                .stream()
                .filter(Objects::nonNull)
                .map(Address::getIp)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    private void checkIfUserExistsAndNotBlocked(final String mail) {

        if (mail.isBlank()) {
            throw new UserCheckNotAllowedException();
        }
        userRepository.findByEmail(mail)
                .orElseThrow(UserNotFoundException::new);
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

            var linkedUserOptional = userRepository.findByEmail(mail);

            if (linkedUserOptional.isPresent()) {
                var linkedUser = linkedUserOptional.get();
                linkedUser.associateNewAddress(dbAddress);
                userRepository.save(linkedUser);
            }
        }
    }

    private void validateIpNotBlocked(final String currentIp) {

        addressRepository.findOneByIp(currentIp)
                .map(Address::isBanned)
                .orElseThrow(IpBannedException::new);
    }


    private void validateUserLinkedIpsNotBanned(final @NonNull LoginFormRequestDto dto) {

        String requestIp = dto.getAddress();

        userRepository.findAddresses(dto.getUserEmail())
                .stream()
                .filter(Objects::nonNull)
                .filter(Address::isBanned)
                .map(Address::getIp)
                .filter(address ->
                        Objects.equals(address, requestIp))
                .findAny()
                .ifPresent(a -> {
                    throw new IpBannedException(requestIp);
                });
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
