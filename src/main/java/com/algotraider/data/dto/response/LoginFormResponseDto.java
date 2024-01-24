package com.algotraider.data.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginFormResponseDto {

    private Boolean status;
    private String userEmail;
    private String info;
    private String address;
    private String proxyAddress;
    private String region;
    private String country;
    private String geo;
    private long loginTime;
    private long loginResponseTime;
}
