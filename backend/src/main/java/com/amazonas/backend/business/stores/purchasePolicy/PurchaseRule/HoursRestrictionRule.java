package com.amazonas.backend.business.stores.purchasePolicy.PurchaseRule;

import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.backend.exceptions.StoreException;

import java.time.LocalTime;
import java.util.List;

public class HoursRestrictionRule implements PurchaseRule {
    private final LocalTime startRestrictionTime;
    private final LocalTime endRestrictionTime;

    public HoursRestrictionRule(LocalTime startRestrictionTime, LocalTime endRestrictionTime) throws StoreException {
        if (startRestrictionTime == null) {
            throw new StoreException("start restriction cannot be empty");
        }
        if (endRestrictionTime == null) {
            throw new StoreException("end restriction cannot be empty");
        }
        if (startRestrictionTime.isAfter(endRestrictionTime)) {
            throw new StoreException("start restriction hour cannot be after end restriction hour");
        }
        this.startRestrictionTime = startRestrictionTime;
        this.endRestrictionTime = endRestrictionTime;
    }

    public LocalTime getStartRestrictionTime() {
        return startRestrictionTime.minusHours(0);
    }

    public LocalTime getEndRestrictionTime() {
        return endRestrictionTime.minusHours(0);
    }


    @Override
    public boolean isSatisfied(List<ProductWithQuantitiy> products, RegisteredUser user) {
        return (LocalTime.now().isAfter(endRestrictionTime) || LocalTime.now().isBefore(startRestrictionTime));
    }

    @Override
    public String generateCFG() {
        String startHour = "" + startRestrictionTime.getHour();
        if (startHour.length() == 1) {
            startHour = "0" + startHour;
        }
        String endHour = "" + endRestrictionTime.getHour();
        if (endHour.length() == 1) {
            endHour = "0" + endHour;
        }
        String startMinute = "" + startRestrictionTime.getMinute();
        if (startMinute.length() == 1) {
            startMinute = "0" + startMinute;
        }
        String endMinute = "" + endRestrictionTime.getMinute();
        if (endMinute.length() == 1) {
            endMinute = "0" + endMinute;
        }
        return "( hour-restriction " + startHour + startMinute + " " + endHour + endMinute + " )";
    }
}
