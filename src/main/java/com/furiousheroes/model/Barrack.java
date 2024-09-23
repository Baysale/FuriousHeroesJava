package com.furiousheroes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Barrack {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int level;
    private int companions;
    private long woodCost;
    private long goldCost;

    public static Barrack defaultBarrack() {
        return Barrack.builder()
                .level(0)
                .companions(0)
                .woodCost(500)
                .goldCost(100)
                .build();
    }
}
