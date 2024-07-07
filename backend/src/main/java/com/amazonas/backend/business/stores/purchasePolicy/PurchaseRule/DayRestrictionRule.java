package com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.exceptions.StoreException;

import java.time.LocalDate;
import java.util.List;

public class DayRestrictionRule implements PurchaseRule {
    private final LocalDate firstRestrictedDay;
    private final LocalDate lastRestrictedDay;

    public DayRestrictionRule(LocalDate firstRestrictedDay, LocalDate lastRestrictedDay) throws StoreException {
        if (firstRestrictedDay == null) {
            throw new StoreException("first restricted date cannot be empty");
        }
        if (lastRestrictedDay == null) {
            throw new StoreException("last restricted date cannot be empty");
        }
        if (firstRestrictedDay.isAfter(lastRestrictedDay)) {
            throw new StoreException("first restricted date cannot be after last restricted date");
        }
        this.firstRestrictedDay = firstRestrictedDay;
        this.lastRestrictedDay = lastRestrictedDay;
    }

    public LocalDate getFirstRestrictedDay() {
        return firstRestrictedDay.minusDays(0);
    }

    public LocalDate getLastRestrictedDay() {
        return lastRestrictedDay.minusDays(0);
    }

    @Override
    public boolean isSatisfied(List<ProductWithQuantitiy> products, RegisteredUser user) {
        return (LocalDate.now().isBefore(firstRestrictedDay) || LocalDate.now().isAfter(lastRestrictedDay));
    }
}
