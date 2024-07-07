package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Rating;

public record StoreRequest(String storeId, String storeName, String storeDescription, Rating storeRating) {

    public StoreRequest(String storeName){
        this("", storeName, "", Rating.FIVE_STARS);
    }
    public static StoreRequest from(String json) {
        return JsonUtils.deserialize(json, StoreRequest.class);
    }

    public String keyword() {
        return storeName;
    }
}
