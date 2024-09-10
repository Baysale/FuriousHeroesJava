package com.furiousheroes.dto;

import lombok.Data;

@Data
public class AuthResponseDTO {
    private Long userId;
    private String accessToken;
    private String tokenType = "Bearer ";

    public AuthResponseDTO(Long userId, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
    }
}
