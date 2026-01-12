package com.consumesafe.consumesafe.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    private String category;

    @Column(name = "made_in")
    private String madeIn;

    private String description;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_tunisian")
    private Boolean isTunisian;
}