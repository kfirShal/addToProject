package com.amazonas.common.requests.store;

import com.amazonas.common.dtos.Product;

public record ProductRequest(String storeId, Product product) {
}
