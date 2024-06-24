package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;

public record StoreStaffRequest(String storeId, String sourceActor, String targetActor){
    public static StoreStaffRequest from(String json) {
        return JsonUtils.deserialize(json, StoreStaffRequest.class);
    }
}
