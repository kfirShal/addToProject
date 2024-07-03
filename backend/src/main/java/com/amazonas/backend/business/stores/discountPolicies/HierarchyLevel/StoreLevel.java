package com.amazonas.backend.business.stores.discountPolicies.HierarchyLevel;

import com.amazonas.common.dtos.Product;

public class StoreLevel implements DiscountHierarchyLevel{
    @Override
    public boolean isTheProductEligible(Product product) {
        return true;
    }
}
