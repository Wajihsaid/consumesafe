package com.consumesafe.consumesafe.controller;


import com.consumesafe.consumesafe.dto.ProductCheckResponse;
import com.consumesafe.consumesafe.model.BoycottProduct;
import com.consumesafe.consumesafe.model.Product;
import com.consumesafe.consumesafe.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/api/check")
    @ResponseBody
    public ResponseEntity<ProductCheckResponse> checkProduct(@RequestParam String product) {
        ProductCheckResponse response = productService.checkProduct(product);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/tunisian-products")
    @ResponseBody
    public ResponseEntity<List<Product>> getTunisianProducts() {
        return ResponseEntity.ok(productService.getAllTunisianProducts());
    }

    @GetMapping("/api/boycott-list")
    @ResponseBody
    public ResponseEntity<List<BoycottProduct>> getBoycottList() {
        return ResponseEntity.ok(productService.getAllBoycottProducts());
    }

    @GetMapping("/api/search")
    @ResponseBody
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        return ResponseEntity.ok(productService.searchProducts(query));
    }
}