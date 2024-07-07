package com.amazonas.backend.business.stores.discountPolicies.HierarchyLevel;

import com.amazonas.common.dtos.Product;

public class CategoryLevel implements DiscountHierarchyLevel{
    private final String category;

    public CategoryLevel (String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    @Override
    public boolean isTheProductEligible(Product product) {
        return product.getCategory().equals(category);
    }
}
