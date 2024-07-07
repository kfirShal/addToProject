package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.Rating;

import java.util.List;

public class SearchRequestBuilder {
    private String productName;
    private List<String> keyWords;
    private Integer minPrice;
    private Integer maxPrice;
    private String productCategory;
    private Rating productRating;

    private SearchRequestBuilder() {
        productName = "";
        keyWords = List.of();
        minPrice = 0;
        maxPrice = Integer.MAX_VALUE;
        productCategory = "";
        productRating = Rating.NOT_RATED;
    }

    public SearchRequestBuilder setProductName(String productName) {
        this.productName = productName.toLowerCase();
        return this;
    }

    public SearchRequestBuilder setKeyWords(List<String> keyWords) {
        this.keyWords = keyWords.stream().map(String::toLowerCase).toList();
        return this;
    }

    public SearchRequestBuilder setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
        return this;
    }

    public SearchRequestBuilder setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public SearchRequestBuilder setProductCategory(String productCategory) {
        this.productCategory = productCategory.toLowerCase();
        return this;
    }

    public SearchRequestBuilder setProductRating(Rating productRating) {
        this.productRating = productRating;
        return this;
    }

    public SearchRequest build() {
        return new SearchRequest(productName, keyWords, minPrice, maxPrice, productCategory, productRating);
    }



    public static SearchRequestBuilder create() {
        return new SearchRequestBuilder();
    }
}