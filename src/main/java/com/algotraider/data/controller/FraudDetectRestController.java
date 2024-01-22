package com.algotraider.data.controller;

import com.algotraider.data.dto.request.UserCheckRequestDto;
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
}
