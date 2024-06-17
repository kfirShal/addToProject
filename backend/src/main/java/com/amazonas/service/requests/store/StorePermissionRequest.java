package com.amazonas.service.requests.store;

public record StorePermissionRequest(String storeId, String targetActor, String action) {
}
