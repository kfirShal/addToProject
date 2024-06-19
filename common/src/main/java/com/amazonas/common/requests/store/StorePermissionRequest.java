package com.amazonas.common.requests.store;

public record StorePermissionRequest(String storeId, String targetActor, String action) {
}
