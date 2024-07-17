package com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.exceptions.StoreException;

import java.util.List;

public class MaxUniqueProductsRule implements PurchaseRule {
    private final int limit;

    public MaxUniqueProductsRule(int limit) throws StoreException {
        if (limit < 0) {
            throw new StoreException("Limit cannot be a negative number");
        }
        this.limit = limit;
    }

    public int getLimit() {
        return limit;
    }

    @Override
    public boolean isSatisfied(List<ProductWithQuantitiy> products, RegisteredUser user) {
        if (products == null) {
            return false;
        }
        return products.size() <= limit;
    }

    @Override
    public String generateCFG() {
        return "( max-unique-products " + limit + " )";
    }
}
