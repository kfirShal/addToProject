package com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.exceptions.StoreException;

import java.util.ArrayList;
import java.util.List;

public class OrRule implements PurchaseRule {
    private final List<PurchaseRule> rules;

    public OrRule(List<PurchaseRule> rules) throws StoreException {
        if (rules == null) {
            throw new StoreException("The list of 'and' rules cannot be empty");
        }
        for (PurchaseRule rule : rules) {
            if (rule == null) {
                throw new StoreException("At least one of the 'and' rules is empty");
            }
        }
        this.rules = rules;
    }

    public List<PurchaseRule> getRules() {
        return new ArrayList<PurchaseRule>(this.rules);

    }

    @Override
    public boolean isSatisfied(List<ProductWithQuantitiy> products, RegisteredUser user) {
        return rules.stream().anyMatch(rule -> rule.isSatisfied(products, user));
    }
}
