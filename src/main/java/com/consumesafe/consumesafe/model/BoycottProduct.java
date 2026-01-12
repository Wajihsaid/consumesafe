package com.consumesafe.consumesafe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boycott_products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoycottProduct {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    private String category;

    private String reason;

    @Column(name = "boycott_level")
    private String boycottLevel; // HIGH, MEDIUM, LOW
}