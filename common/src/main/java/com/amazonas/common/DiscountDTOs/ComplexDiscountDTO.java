package com.amazonas.common.DiscountDTOs;

public record ComplexDiscountDTO(DiscountConditionDTO discountCondition,
                                 DiscountComponentDTO discountComponentDTO)
        implements DiscountComponentDTO {
}
