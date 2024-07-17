package com.amazonas.common.requests.stores;

import com.amazonas.common.utils.Rating;

import java.util.List;

public class ProductSearchRequestBuilder {
    private String productName;
    private List<String> keyWords;
    private Integer minPrice;
    private Integer maxPrice;
    private String productCategory;
    private Rating productRating;

    private ProductSearchRequestBuilder() {
        productName = "";
        keyWords = List.of();
        minPrice = 0;
        maxPrice = Integer.MAX_VALUE;
        productCategory = "";
        productRating = Rating.NOT_RATED;
    }

    public ProductSearchRequestBuilder setProductName(String productName) {
        this.productName = productName.toLowerCase();
        return this;
    }

    public ProductSearchRequestBuilder setKeyWords(List<String> keyWords) {
        this.keyWords = keyWords.stream().map(String::toLowerCase).toList();
        return this;
    }

    public ProductSearchRequestBuilder setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
        return this;
    }

    public ProductSearchRequestBuilder setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
        return this;
    }

    public ProductSearchRequestBuilder setProductCategory(String productCategory) {
        this.productCategory = productCategory.toLowerCase();
        return this;
    }

    public ProductSearchRequestBuilder setProductRating(Rating productRating) {
        this.productRating = productRating;
        return this;
    }

    public ProductSearchRequest build() {
        return new ProductSearchRequest(productName, keyWords, minPrice, maxPrice, productCategory, productRating);
    }

    public static ProductSearchRequestBuilder create() {
        return new ProductSearchRequestBuilder();
    }
}