package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Rating;

import java.util.List;

public record SearchRequest(String productName, List<String> keyWords, Integer minPrice, Integer maxPrice,
                            String productCategory, Rating productRating) {

    public SearchRequest(String productName, List<String> keyWords, Integer minPrice, Integer maxPrice, String productCategory, Rating productRating) {
        this.productName = productName.toLowerCase();
        this.keyWords = keyWords.stream().map(String::toLowerCase).toList();
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.productCategory = productCategory.toLowerCase();
        this.productRating = productRating;
    }
    public SearchRequest(String keyWords){
        this("", List.of(keyWords.toLowerCase()), 0, Integer.MAX_VALUE, "", Rating.FIVE_STARS);
    }

    public static SearchRequest from(String json) {
        return JsonUtils.deserialize(json, SearchRequest.class);
    }

    public String keyword() {
        return keyWords.getFirst();
    }
}
