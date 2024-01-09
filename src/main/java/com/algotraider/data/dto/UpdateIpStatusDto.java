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
public class UpdateIpStatusDto {

    private String source;
    private String address;
    private Boolean status;
    private long lastUpdated;
}
