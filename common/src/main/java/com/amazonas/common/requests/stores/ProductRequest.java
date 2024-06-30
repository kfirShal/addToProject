package com.amazonas.common.requests.stores;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.utils.JsonUtils;

public record ProductRequest(String storeId, Product product,Integer quantity) {
    public ProductRequest(String storeId, Product product) {
        this(storeId, product, -1);
    }

    public static ProductRequest from(String json) {
        return JsonUtils.deserialize(json, ProductRequest.class);
    }
}
