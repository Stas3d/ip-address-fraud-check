package com.algotraider.data.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class LoginDto {

    private String userEmail;
    private String info;
    private String address;
    private String proxyAddress;
    private String region;
    private String country;
    private String geo;
    private Timestamp loginTime;
}
