package com.algotraider.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class IpCheckResponseDto {

    private String source;
    private String address;
    private Boolean ipBannedStatus;
    private Long timeStampMillis;
}
