package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;

public record SearchInStoreRequest(String storeId, SearchRequest searchRequest) {
    public static SearchInStoreRequest from(String json) {
        return JsonUtils.deserialize(json, SearchInStoreRequest.class);
    }
}
