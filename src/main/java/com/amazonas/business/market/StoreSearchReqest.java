package com.amazonas.business.market;

import com.amazonas.business.stores.Rating;

import java.util.List;

public class StoreSearchReqest extends GlobalSearchRequest {
    public StoreSearchReqest(String productName, List<String> keyWords, Integer minPrice, Integer maxPrice, String productCategory, Rating productRating, Rating storeRating) {
        super(productName, keyWords, minPrice, maxPrice, productCategory, productRating, storeRating);
    }
}
