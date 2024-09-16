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
public class Smithy {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int level;
    private int weapons;
    private long woodCost;
    private long metalCost;

    @OneToOne(mappedBy = "smithy", cascade = CascadeType.ALL, orphanRemoval = true)
    private User user;

    public static Smithy defaultSmithy() {
        return Smithy.builder()
                .level(0)
                .weapons(0)
                .woodCost(500)
                .metalCost(100)
                .build();
    }
}
