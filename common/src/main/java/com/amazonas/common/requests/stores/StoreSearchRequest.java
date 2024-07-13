package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Rating;

public record StoreSearchRequest(String storeId, String storeName, String storeDescription, Rating storeRating) {

    public StoreSearchRequest(String storeName){
        this("", storeName, "", Rating.NOT_RATED);
    }

    public StoreSearchRequest(String storeId, String storeName) {
        this(storeId, "", "", Rating.NOT_RATED);
    }
    public static StoreSearchRequest from(String json) {
        return JsonUtils.deserialize(json, StoreSearchRequest.class);
    }

}
