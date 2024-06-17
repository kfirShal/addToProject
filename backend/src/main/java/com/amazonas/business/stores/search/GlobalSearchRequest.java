package com.amazonas.business.stores.search;

import com.amazonas.common.utils.Rating;

import java.util.List;

public class GlobalSearchRequest extends SearchRequest {
    private final Rating storeRating;

    public GlobalSearchRequest(String productName, List<String> keyWords, Integer minPrice, Integer maxPrice, String productCategory, Rating productRating, Rating storeRating) {
        super(productName, keyWords, minPrice, maxPrice, productCategory, productRating);
        this.storeRating = storeRating;
    }

    public Rating getStoreRating() {
        return storeRating;
    }
}
