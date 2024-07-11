package com.amazonas.common.dtos;

import com.amazonas.common.utils.Rating;

public record StoreDetails(String storeId, String storeName, String storeDescription, Rating storeRating) {
    //storeGrid.setColumns("storeId", "storeName", "storeRating", "storeDescription");
    public String getStoreId() {
        return storeId;
    }
    public String getStoreName() {
        return storeName;
    }
    public String getStoreDescription() {
        return storeDescription;
    }
    public Rating getStoreRating() {
        return storeRating;
    }
}
