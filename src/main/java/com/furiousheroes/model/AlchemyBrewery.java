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
public class AlchemyBrewery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int level;
    private int herbs;
    private long woodCost;
    private long herbCost;

    public static AlchemyBrewery defaultAlchemyBrewery() {
        return AlchemyBrewery.builder()
                .level(0)
                .herbs(0)
                .woodCost(500)
                .herbCost(100)
                .build();
    }
}
