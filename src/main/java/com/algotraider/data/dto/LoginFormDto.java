package com.algotraider.data.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginFormDto {

    private String userEmail;
    private String info;
    private String address;
    private String proxyAddress;
    private String region;
    private String country;
    private String geo;
    private long loginTime;
}
