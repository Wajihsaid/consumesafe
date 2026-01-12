package com.consumesafe.consumesafe.dto;

import com.consumesafe.consumesafe.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCheckResponse {
    private boolean isBoycotted;
    private String message;
    private String boycottLevel;
    private String reason;
    private List<Product> alternatives;
}