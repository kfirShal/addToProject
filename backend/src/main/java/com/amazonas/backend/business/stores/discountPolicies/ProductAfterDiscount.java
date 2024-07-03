package com.amazonas.backend.business.stores.discountPolicies;

public record ProductAfterDiscount(
        String productId,
        int quantity,
        double originalPrice,
        double priceAfterDiscount
) { }
