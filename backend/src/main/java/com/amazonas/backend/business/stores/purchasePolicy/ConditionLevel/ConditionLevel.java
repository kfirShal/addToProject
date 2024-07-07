package com.amazonas.backend.business.stores.purchasePolicy.ConditionLevel;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;

import java.util.List;

public interface ConditionLevel {
    boolean containsTypeOfPurchase(List<ProductWithQuantitiy> products);
}
