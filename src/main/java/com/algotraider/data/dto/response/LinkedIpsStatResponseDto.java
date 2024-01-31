package com.algotraider.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LinkedIpsStatResponseDto {

    private String source;
    private String email;
    private Boolean status;
    private Long timeStamp;
    private List<String> linkedIpsList;
}
