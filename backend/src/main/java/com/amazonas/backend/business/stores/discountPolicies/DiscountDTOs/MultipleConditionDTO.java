package com.amazonas.backend.business.stores.discountPolicies.DiscountDTOs;

import java.util.List;

public record MultipleConditionDTO(MultipleConditionType multipleConditionType,
                                   List<DiscountConditionDTO> conditions)
        implements DiscountConditionDTO{
}
