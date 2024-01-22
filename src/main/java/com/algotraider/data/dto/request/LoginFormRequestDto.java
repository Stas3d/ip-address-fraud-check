package com.algotraider.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoginFormRequestDto {

    private String userEmail;
    private String info;
    private String address;
    private String proxyAddress;
    private String region;
    private String country;
    private String geo;
    private long loginTime;
}
