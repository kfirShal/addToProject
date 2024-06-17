package com.amazonas.backend.business.stores.search;

import com.amazonas.common.utils.Rating;

import java.util.List;

public class SearchRequest {
    private final String productName;
    private final List<String> keyWords;
    private final Integer minPrice;
    private final Integer maxPrice;
    private final String productCategory;
    private final Rating productRating;

    public SearchRequest(String productName, List<String> keyWords, Integer minPrice, Integer maxPrice, String productCategory, Rating productRating) {
        this.productName = productName.toLowerCase();
        this.keyWords = keyWords.stream().map(String::toLowerCase).toList();
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.productCategory = productCategory.toLowerCase();
        this.productRating = productRating;
    }

    public String getProductName() {
        return productName;
    }

    public List<String> getKeyWords() {
        return keyWords;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public Rating getProductRating() {
        return productRating;
    }
}
