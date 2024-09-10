package com.furiousheroes.dto;

import lombok.Data;

@Data
public class UpdateUserDTO {
    private String newUsername;
    private String newPassword;
    private String newEmail;
}
