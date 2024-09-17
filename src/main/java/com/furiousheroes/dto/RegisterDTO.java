package com.furiousheroes.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String password;
    private String email;
    private String type;
    private String owlColor;
    private String eyeShape;
    private String eyeColor;
    private String clothing;
    private String weapon;
    private String hobby;
    private String vacationSpot;
}
