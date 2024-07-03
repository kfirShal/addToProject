package com.amazonas.frontend.model;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.utils.Rating;

import java.util.List;

public record Store(String storeName, String description, Rating rating, List<Product> products) {
}
