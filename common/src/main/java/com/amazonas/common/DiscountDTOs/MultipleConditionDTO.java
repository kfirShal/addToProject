package com.amazonas.common.DiscountDTOs;

import java.util.List;

public record MultipleConditionDTO(MultipleConditionType multipleConditionType,
                                   List<DiscountConditionDTO> conditions)
        implements DiscountConditionDTO{
}
