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
public class Stall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int level;
    private int mounts;
    private long woodCost;
    private long goldCost;

    public static Stall defaultStall() {
        return Stall.builder()
                .level(0)
                .mounts(0)
                .woodCost(500)
                .goldCost(100)
                .build();
    }
}
