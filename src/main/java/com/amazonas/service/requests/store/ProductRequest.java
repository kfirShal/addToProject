package com.amazonas.service.requests.store;

import com.amazonas.business.inventory.Product;

public record ProductRequest(String storeId, Product product) {
}
