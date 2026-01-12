package com.consumesafe.consumesafe.service;


import com.consumesafe.consumesafe.model.BoycottProduct;
import com.consumesafe.consumesafe.model.Product;
import com.consumesafe.consumesafe.repository.BoycottProductRepository;
import com.consumesafe.consumesafe.repository.ProductRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvLoaderService {

    private final ProductRepository productRepository;
    private final BoycottProductRepository boycottProductRepository;

    @PostConstruct
    public void loadData() {
        loadBoycottProducts();
        loadTunisianProducts();
    }

    private void loadBoycottProducts() {
        try {
            if (boycottProductRepository.count() > 0) {
                log.info("Boycott products already loaded");
                return;
            }

            ClassPathResource resource = new ClassPathResource("data/boycott_products.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()));
            List<String[]> rows = reader.readAll();

            // Skip header
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                BoycottProduct product = new BoycottProduct();
                product.setName(row[0]);
                product.setBrand(row[1]);
                product.setCategory(row[2]);
                product.setReason(row[3]);
                product.setBoycottLevel(row[4]);
                boycottProductRepository.save(product);
            }

            log.info("Loaded {} boycott products", rows.size() - 1);
        } catch (IOException | CsvException e) {
            log.error("Error loading boycott products", e);
        }
    }

    private void loadTunisianProducts() {
        try {
            if (productRepository.count() > 0) {
                log.info("Tunisian products already loaded");
                return;
            }

            ClassPathResource resource = new ClassPathResource("data/tunisian_products.csv");
            CSVReader reader = new CSVReader(new InputStreamReader(resource.getInputStream()));
            List<String[]> rows = reader.readAll();

            // Skip header
            for (int i = 1; i < rows.size(); i++) {
                String[] row = rows.get(i);
                Product product = new Product();
                product.setName(row[0]);
                product.setBrand(row[1]);
                product.setCategory(row[2]);
                product.setMadeIn(row[3]);
                product.setDescription(row[4]);
                product.setImageUrl(row[5]);
                product.setIsTunisian(true);
                productRepository.save(product);
            }

            log.info("Loaded {} Tunisian products", rows.size() - 1);
        } catch (IOException | CsvException e) {
            log.error("Error loading Tunisian products", e);
        }
    }
}