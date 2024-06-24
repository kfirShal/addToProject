package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Rating;

public record GlobalSearchRequest(Rating storeRating, SearchRequest searchRequest) {
    public static GlobalSearchRequest from(String json) {
        return JsonUtils.deserialize(json, GlobalSearchRequest.class);
    }
}
