package com.algotraider.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCheckResponseDto {

    private String source;
    private String userEmail;
    private Boolean userStatus;
    private Long timeStampMillis;
}
