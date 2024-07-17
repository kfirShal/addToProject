package com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.userProfiles.RegisteredUser;

import java.util.List;

public interface PurchaseRule {
    boolean isSatisfied(List<ProductWithQuantitiy> products, RegisteredUser user);
    String generateCFG();
}
