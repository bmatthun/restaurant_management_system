package com.example.demo.model;

import com.example.demo.model.enums.UnitTypes;
import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "Ingredient")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Összetevő_neve")
    private String name;

    @Column(name = "Mennyiség")
    private Integer quantity;

    @Column(name = "Mértékegység")
    @Enumerated(EnumType.STRING)
    private UnitTypes unit;

    @ManyToMany(mappedBy = "ingredients")
    private List<Bowl> bowls;
}
