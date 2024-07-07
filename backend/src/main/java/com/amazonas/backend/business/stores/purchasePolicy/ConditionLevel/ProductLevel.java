package com.amazonas.backend.business.stores.purchasePolicy.ConditionLevel;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.exceptions.StoreException;

import java.util.List;

public class ProductLevel implements ConditionLevel {
    private final String productId;
    private final int quantity;

    public ProductLevel(String productId, int quantity) throws StoreException {
        if (productId == null || productId.isEmpty()) {
            throw new StoreException("product ID cannot be empty");
        }
        this.productId = productId;
        this.quantity = quantity;
    }


    @Override
    public boolean containsTypeOfPurchase(List<ProductWithQuantitiy> products) {
        int productCounter = 0;
        for (ProductWithQuantitiy productWithQuantitiy : products) {
            if (productWithQuantitiy.product().productId().equals(productId)) {
                productCounter += productWithQuantitiy.quantity();
            }
        }
        return productCounter >= quantity;
    }
}
