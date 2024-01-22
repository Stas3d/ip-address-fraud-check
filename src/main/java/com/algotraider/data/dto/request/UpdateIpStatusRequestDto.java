package com.algotraider.data.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateIpStatusRequestDto {

    private String source;
    private String address;
    private Boolean status;
    private long lastUpdated;
}
