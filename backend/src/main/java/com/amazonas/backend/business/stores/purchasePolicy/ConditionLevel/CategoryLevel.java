package com.amazonas.backend.business.stores.purchasePolicy.ConditionLevel;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.exceptions.StoreException;

import java.util.List;

public class CategoryLevel implements ConditionLevel {
    private final String categoryName;
    private final int quantity;

    public CategoryLevel(String categoryName, int quantity) throws StoreException {
        if (categoryName == null || categoryName.isEmpty()) {
            throw new StoreException("categoryName cannot be null or empty");
        }
        this.categoryName = categoryName;
        this.quantity = quantity;
    }


    @Override
    public boolean containsTypeOfPurchase(List<ProductWithQuantitiy> products) {
        int categoryCounter = 0;
        for (ProductWithQuantitiy productWithQuantitiy : products) {
            if (productWithQuantitiy.product().category().equals(categoryName)) {
                categoryCounter += productWithQuantitiy.quantity();
            }
        }
        return categoryCounter >= quantity;
    }
}
