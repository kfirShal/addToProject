package com.amazonas.backend.business.stores.discountPolicies;

import com.amazonas.common.dtos.Product;

public record ProductWithQuantitiy(
        Product product,
        int quantity
) {
}
