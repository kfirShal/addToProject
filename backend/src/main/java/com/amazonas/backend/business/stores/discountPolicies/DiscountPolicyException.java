package com.amazonas.backend.business.stores.discountPolicies;

public class DiscountPolicyException extends Exception {
    public int startIndex;
    public int endIndex;
    public DiscountPolicyException(String message, int startIndex, int endIndex) {
        super(message);
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }
}
