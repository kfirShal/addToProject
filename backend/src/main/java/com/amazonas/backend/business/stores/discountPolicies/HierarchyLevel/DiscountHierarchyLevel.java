package com.amazonas.backend.business.stores.discountPolicies.HierarchyLevel;

import com.amazonas.common.dtos.Product;

public interface DiscountHierarchyLevel {
    boolean isTheProductEligible(Product product);
}
