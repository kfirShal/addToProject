package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Rating;

import java.util.List;

public record ProductSearchRequest(String productName, List<String> keyWords, Integer minPrice, Integer maxPrice,
                                   String productCategory, Rating productRating) {

    public ProductSearchRequest(String productName, List<String> keyWords, Integer minPrice, Integer maxPrice, String productCategory, Rating productRating) {
        this.productName = productName.toLowerCase();
        this.keyWords = keyWords.stream().map(String::toLowerCase).toList();
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.productCategory = productCategory.toLowerCase();
        this.productRating = productRating;
    }
    public static ProductSearchRequest from(String json) {
        return JsonUtils.deserialize(json, ProductSearchRequest.class);
    }
}
