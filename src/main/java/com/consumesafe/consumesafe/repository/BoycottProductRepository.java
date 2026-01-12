package com.consumesafe.consumesafe.repository;

import com.consumesafe.consumesafe.model.BoycottProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoycottProductRepository extends JpaRepository<BoycottProduct, Long> {
    Optional<BoycottProduct> findByNameIgnoreCase(String name);
    Optional<BoycottProduct> findByBrandIgnoreCase(String brand);
}