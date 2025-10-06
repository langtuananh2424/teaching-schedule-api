package com.thuyloiuni.teaching_schedule_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtResponseDTO {
    private String accessToken;
    private String tokenType = "Bearer";
    public JwtResponseDTO(String accessToken) {
        this.accessToken = accessToken;
    }
}
