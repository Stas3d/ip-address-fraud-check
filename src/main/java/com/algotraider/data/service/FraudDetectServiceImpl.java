package com.algotraider.data.service;

import com.algotraider.data.dto.CheckIpDto;
import com.algotraider.data.dto.CheckUserDto;
import com.algotraider.data.dto.LoginDto;
import com.algotraider.data.dto.UpdateIpStatusDto;
import com.algotraider.data.entity.Address;
import com.algotraider.data.exception.UserCheckNotAllowedException;
import com.algotraider.data.repo.UserRepository;
import com.algotraider.data.repo.AddressRepository;

import com.algotraider.data.validation.ValidationService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FraudDetectServiceImpl implements FraudDetectService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ValidationService validation;

    public boolean isUserBanned(final @NonNull CheckUserDto dto) {

        validation.validateEmail(dto.getUserEmail());
        var user = userRepository.findByEmail(dto.getUserEmail());
        return (user != null) ? user.isBanned() : Boolean.FALSE;
    }

    public boolean isIpAddressBanned(final @NonNull CheckIpDto dto) {

        validation.validateIp(dto.getAddress());
        var address = addressRepository.findOneByIp(dto.getAddress());
        return (address != null) ? address.isBanned() : Boolean.FALSE;
    }

    public String updateIpAddressStatus(final @NonNull UpdateIpStatusDto dto) {

        validation.validateIp(dto.getAddress());
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

    public boolean processLogin(final @NonNull LoginDto loginDtoForm) {

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

    private void saveNewIpUserRelation(final LoginDto loginDtoForm) {

        Address address = new Address(loginDtoForm.getAddress(), "");
        var linkedUser = userRepository.findByEmail(loginDtoForm.getUserEmail());
        addressRepository.save(address);
    }

    private Boolean checkIfIpNotBlocked(final String currentIp) {

        var address = addressRepository.findOneByIp(currentIp);
        return (address != null) ? !address.isBanned() : Boolean.TRUE;
    }
}
