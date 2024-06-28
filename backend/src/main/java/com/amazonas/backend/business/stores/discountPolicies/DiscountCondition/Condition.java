package com.amazonas.backend.business.stores.discountPolicies.DiscountCondition;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;

import java.util.List;

public interface Condition {
    boolean decideCondition(List<ProductWithQuantitiy> products);
}
