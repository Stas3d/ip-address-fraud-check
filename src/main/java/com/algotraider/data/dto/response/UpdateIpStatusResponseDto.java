package com.algotraider.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateIpStatusResponseDto {

    private String source;
    private String address;
    private Boolean updatedStatus;
    private Long timeStampMillis;
}
