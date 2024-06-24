package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;

public record StorePermissionRequest(String storeId, String targetActor, String action) {
    public static StorePermissionRequest from(String json) {
        return JsonUtils.deserialize(json, StorePermissionRequest.class);
    }
}
