package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.Rating;

public class StoreDetailsRequestBuilder {
    //String storeId, String storeName, String storeDescription, Rating storeRating
    private String storeId;
    private String storeName;
    private String storeDescription;
    private Rating storeRating;

    private StoreDetailsRequestBuilder() {
        storeId = "";
        storeName = "";
        storeDescription = "";
        storeRating = Rating.NOT_RATED;
    }

    // setteres
    public StoreDetailsRequestBuilder setStoreId(String storeId) {
        this.storeId = storeId;
        return this;
    }

    public StoreDetailsRequestBuilder setStoreName(String storeName) {
        this.storeName = storeName;
        return this;
    }

    public StoreDetailsRequestBuilder setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
        return this;
    }

    public StoreDetailsRequestBuilder setStoreRating(Rating storeRating) {
        this.storeRating = storeRating;
        return this;
    }

    // build
    public StoreSearchRequest build() {
        return new StoreSearchRequest(storeId, storeName, storeDescription, storeRating);
    }

    // create static
    public static StoreDetailsRequestBuilder create() {
        return new StoreDetailsRequestBuilder();
    }


}
