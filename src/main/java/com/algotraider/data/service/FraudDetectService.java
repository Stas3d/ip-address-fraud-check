package com.algotraider.data.service;

import com.algotraider.data.dto.CheckIpDto;
import com.algotraider.data.dto.CheckUserDto;
import com.algotraider.data.dto.LoginDto;
import com.algotraider.data.dto.UpdateIpStatusDto;

public interface FraudDetectService {

    boolean isUserBanned(CheckUserDto dto);

    boolean isIpAddressBanned(CheckIpDto dto);

    String updateIpAddressStatus(UpdateIpStatusDto dto);

    boolean processLogin(LoginDto dto);
}
