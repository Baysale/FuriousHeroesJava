package com.furiousheroes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "APP_USER")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String password;
    private String email;
    private String type;
    private long experience;
    private long gold;
    private long wood;
    private long herbs;
    private long metals;
    @Lob
    private String avatarImage;

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private List<Role> roles = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "smithy_id")
    private Smithy smithy;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "alchemy_brewery_id")
    private AlchemyBrewery alchemyBrewery;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "barrack_id")
    private Barrack barrack;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "stall_id")
    private Stall stall;
}
