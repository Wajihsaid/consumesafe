package com.consumesafe.consumesafe.service;


import com.consumesafe.consumesafe.dto.ProductCheckResponse;
import com.consumesafe.consumesafe.model.BoycottProduct;
import com.consumesafe.consumesafe.model.Product;
import com.consumesafe.consumesafe.repository.BoycottProductRepository;
import com.consumesafe.consumesafe.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final BoycottProductRepository boycottProductRepository;

    public ProductCheckResponse checkProduct(String productName) {
        ProductCheckResponse response = new ProductCheckResponse();

        // Check if product is boycotted
        Optional<BoycottProduct> boycottProduct =
                boycottProductRepository.findByNameIgnoreCase(productName);

        if (boycottProduct.isPresent()) {
            BoycottProduct bp = boycottProduct.get();
            response.setBoycotted(true);
            response.setMessage("⚠️ Ce produit est boycotté!");
            response.setBoycottLevel(bp.getBoycottLevel());
            response.setReason(bp.getReason());

            // Get Tunisian alternatives
            List<Product> alternatives =
                    productRepository.findByCategory(bp.getCategory());
            response.setAlternatives(alternatives);
        } else {
            response.setBoycotted(false);
            response.setMessage("✅ Ce produit n'est pas boycotté");
        }

        return response;
    }

    public List<Product> getAllTunisianProducts() {
        return productRepository.findByIsTunisianTrue();
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

    public List<BoycottProduct> getAllBoycottProducts() {
        return boycottProductRepository.findAll();
    }
}