package com.amazonas.service.requests.users;

public record CartRequest(String storeId, String productId, Integer quantity) {
}
