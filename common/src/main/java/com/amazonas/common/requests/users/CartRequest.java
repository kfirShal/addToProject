package com.amazonas.common.requests.users;

import com.amazonas.common.utils.JsonUtils;

public record CartRequest(String storeId, String productId, Integer quantity) {
    public static CartRequest from(String json) {
        return JsonUtils.deserialize(json, CartRequest.class);
    }
}
