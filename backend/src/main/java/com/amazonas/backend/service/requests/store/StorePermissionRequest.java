package com.amazonas.backend.service.requests.store;

public record StorePermissionRequest(String storeId, String targetActor, String action) {
}
