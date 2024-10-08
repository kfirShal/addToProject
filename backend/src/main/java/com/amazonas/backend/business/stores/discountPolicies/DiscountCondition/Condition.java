package com.amazonas.backend.business.stores.discountPolicies.DiscountCondition;

import com.amazonas.common.DiscountDTOs.DiscountConditionDTO;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.exceptions.StoreException;

import java.util.List;

public interface Condition {
    boolean decideCondition(List<ProductWithQuantitiy> products);
    DiscountConditionDTO generateDTO() throws StoreException;
    String generateCFG() throws StoreException;
}
