package com.algotraider.data.controller;

import com.algotraider.data.dto.request.AddressCheckRequestDto;
import com.algotraider.data.dto.request.LoginFormRequestDto;
import com.algotraider.data.dto.request.UpdateIpStatusRequestDto;
import com.algotraider.data.dto.request.UserCheckRequestDto;
import com.algotraider.data.dto.response.IpCheckResponseDto;
import com.algotraider.data.dto.response.LoginFormResponseDto;
import com.algotraider.data.dto.response.UpdateIpStatusResponseDto;
import com.algotraider.data.dto.response.UserCheckResponseDto;
import com.algotraider.data.service.FraudDetectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/fraud-check")
@RequiredArgsConstructor
public class FraudDetectRestController {

    private final FraudDetectService service;

    @GetMapping("/user-banned-status")
    public @ResponseBody ResponseEntity<UserCheckResponseDto> getUserBannedStatus(
            @RequestBody UserCheckRequestDto dto) {

        var status = service.checkIfUserBanned(dto);

        return new ResponseEntity<>(
                UserCheckResponseDto.builder()
                        .source(dto.getSource())
                        .userEmail(dto.getUserEmail())
                        .userStatus(status)
                        .timeStampMillis(Instant.now().toEpochMilli())
                        .build(),
                HttpStatus.OK);
    }

    @GetMapping("/ip-banned-status")
    public @ResponseBody ResponseEntity<IpCheckResponseDto> getIpBannedStatus(
            @RequestBody AddressCheckRequestDto dto) {

        var status = service.checkIfIpAddressBanned(dto);

        return new ResponseEntity<>(
                IpCheckResponseDto.builder()
                        .source(dto.getSource())
                        .address(dto.getAddress())
                        .ipBannedStatus(status)
                        .timeStampMillis(Instant.now().toEpochMilli())
                        .build(),
                HttpStatus.OK);
    }

    @PostMapping("/update-ip-status")
    public @ResponseBody ResponseEntity<UpdateIpStatusResponseDto> updateIpAddressStatus(
            @RequestBody UpdateIpStatusRequestDto requestDto) {

        var responseDto = service.updateIpAddressStatus(requestDto);

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }

    @GetMapping("/login")
    public @ResponseBody ResponseEntity<LoginFormResponseDto> processLogin(
            @RequestBody LoginFormRequestDto requestDto) {

        Boolean result = service.processLogin(requestDto);

        LoginFormResponseDto responseDto = LoginFormResponseDto.builder()
                .status(result)
                .userEmail(requestDto.getUserEmail())
                .info(requestDto.getInfo())
                .address(requestDto.getAddress())
                .proxyAddress(requestDto.getProxyAddress())
                .region(requestDto.getRegion())
                .country(requestDto.getCountry())
                .geo(requestDto.getGeo())
                .loginTime(requestDto.getLoginTime())
                .loginResponseTime(Instant.now().toEpochMilli())
                .build();

        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
