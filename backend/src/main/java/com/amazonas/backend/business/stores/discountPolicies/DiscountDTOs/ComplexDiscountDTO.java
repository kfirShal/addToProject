package com.amazonas.backend.business.stores.discountPolicies.DiscountDTOs;

public record ComplexDiscountDTO(DiscountConditionDTO discountCondition,
                                 DiscountComponentDTO discountComponentDTO)
        implements DiscountComponentDTO {
}
