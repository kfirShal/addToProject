package com.amazonas.backend.service.requests.store;

import com.amazonas.backend.business.inventory.Product;

public record ProductRequest(String storeId, Product product) {
}
