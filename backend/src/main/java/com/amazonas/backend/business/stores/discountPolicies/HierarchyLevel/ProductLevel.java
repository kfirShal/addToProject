package com.amazonas.backend.business.stores.discountPolicies.HierarchyLevel;

import com.amazonas.common.dtos.Product;

public class ProductLevel implements DiscountHierarchyLevel{
    private final String productId;

    public ProductLevel (String productId) {
        this.productId = productId;
    }

    public String getProductId() {
        return productId;
    }

    @Override
    public boolean isTheProductEligible(Product product) {
        return productId.equals(product.getProductId());
    }
}
