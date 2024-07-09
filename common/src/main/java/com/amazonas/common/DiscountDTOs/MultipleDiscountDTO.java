package com.amazonas.common.DiscountDTOs;

import java.util.List;

public record MultipleDiscountDTO(List<DiscountComponentDTO> discountComponents,
                                  MultipleDiscountType multipleDiscountType)
        implements DiscountComponentDTO {
}
