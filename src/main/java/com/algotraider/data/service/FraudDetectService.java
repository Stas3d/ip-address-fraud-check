package com.algotraider.data.service;

import com.algotraider.data.dto.AddressCheckRequestDto;
import com.algotraider.data.dto.UserCheckRequestDto;
import com.algotraider.data.dto.LoginFormDto;
import com.algotraider.data.dto.UpdateIpStatusDto;

public interface FraudDetectService {

    boolean checkIfUserBanned(UserCheckRequestDto dto);

    boolean checkIfIpAddressBanned(AddressCheckRequestDto dto);

    String updateIpAddressStatus(UpdateIpStatusDto dto);

    boolean processLogin(LoginFormDto dto);
}
