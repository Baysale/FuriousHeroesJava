package com.furiousheroes.dto;

import jakarta.persistence.Lob;
import lombok.Data;

@Data
public class AuthResponseDTO {
    private Long userId;
    private String accessToken;
    private String tokenType = "Bearer ";
    @Lob
    private String avatarImage;

    public AuthResponseDTO(Long userId, String accessToken, String avatarImage) {
        this.userId = userId;
        this.accessToken = accessToken;
        this.avatarImage = avatarImage;
    }
}
