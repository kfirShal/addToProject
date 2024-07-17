package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;

public record DiscountCFGRequest(String StoreID, String cfg) {
    public static DiscountCFGRequest from(String payload) {
        return JsonUtils.deserialize(payload,DiscountCFGRequest.class);
    }
}
