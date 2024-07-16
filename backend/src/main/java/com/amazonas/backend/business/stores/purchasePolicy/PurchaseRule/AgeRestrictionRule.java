package com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.exceptions.StoreException;

import java.time.LocalDate;
import java.util.List;

public class AgeRestrictionRule implements PurchaseRule {
    private final int minAge;

    public AgeRestrictionRule(int minAge) throws StoreException {
        if (minAge < 0)
            throw new StoreException("Minimum age cannot be negative");
        this.minAge = minAge;
    }

    public int getMinAge() {
        return minAge;
    }

    @Override
    public boolean isSatisfied(List<ProductWithQuantitiy> products, RegisteredUser user) {
        return user.getBirthDate().isBefore(LocalDate.now().minusYears(minAge).plusDays(1));
    }
}
