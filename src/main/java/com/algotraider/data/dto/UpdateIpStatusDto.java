package com.algotraider.data.dto;

import lombok.Data;

import java.sql.Timestamp;

@Data
public class UpdateIpStatusDto {

    private String source;
    private String address;
    private Boolean status;
    private Timestamp lastUpdated;
}
