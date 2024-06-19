package com.amazonas.common.requests.users;

public record CartRequest(String storeId, String productId, Integer quantity) {
}
