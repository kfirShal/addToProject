package com.amazonas.backend.service.requests.users;

public record CartRequest(String storeId, String productId, Integer quantity) {
}
