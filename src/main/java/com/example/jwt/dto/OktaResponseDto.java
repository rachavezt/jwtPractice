package com.example.jwt.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OktaResponseDto {
    private String url;
    private String base64Payload;
}
