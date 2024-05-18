package com.amazonas.business.stores;

import java.util.List;

public class SearchRequest {
    private final String productName;
    private final List<String> keyWords;
    private final Integer minPrice;
    private final Integer maxPrice;
    private final String productCategory;
    private final Rating productRating;

    public SearchRequest(String productName, List<String> keyWords, Integer minPrice, Integer maxPrice, String productCategory, Rating productRating) {
        this.productName = productName;
        this.keyWords = keyWords;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.productCategory = productCategory;
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
