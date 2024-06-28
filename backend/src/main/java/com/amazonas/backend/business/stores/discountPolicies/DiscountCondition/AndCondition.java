package com.amazonas.backend.business.stores.discountPolicies.DiscountCondition;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;

import java.util.List;

public class AndCondition implements Condition{

    final private Condition[] conditions;
    public AndCondition(Condition[] conditions) {
        if (conditions == null || conditions.length == 0) {
            throw new IllegalArgumentException("conditions cannot empty");
        }
        this.conditions = new Condition[conditions.length];
        for (int i = 0; i < conditions.length; i++) {
            if (conditions[i] == null) {
                throw new IllegalArgumentException("conditions cannot be null");
            }
            this.conditions[i] = conditions[i];
        }
    }

    @Override
    public boolean decideCondition(List<ProductWithQuantitiy> products) {
        if (products == null ) {
            throw new IllegalArgumentException("products list cannot be null");
        }
        for (Condition condition : conditions) {
            if (!condition.decideCondition(products)) {
                return false;
            }
        }
        return true;
    }
}
