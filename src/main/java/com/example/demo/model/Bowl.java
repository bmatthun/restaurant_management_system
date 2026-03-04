package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "Bowl")
public class Bowl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Tál_típusa")
    private String name;

    @ManyToMany(
            fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "bowl_ingredients",
    joinColumns = @JoinColumn(name = "bowl_id"),
    inverseJoinColumns = @JoinColumn(name = "ingredient_id"))
    private List<Ingredient> ingredients;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "Mennyiség")
    private Integer quantity;

    @Column(name = "Mértékegység")
    private String unit = "db";

    @Column(name = "Ár")
    private Integer price;
}
