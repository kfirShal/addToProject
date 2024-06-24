package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;

public record StoreCreationRequest(String description, String storeName, String ownerId) {
    public static StoreCreationRequest from(String json) {
        return JsonUtils.deserialize(json, StoreCreationRequest.class);
    }
}
