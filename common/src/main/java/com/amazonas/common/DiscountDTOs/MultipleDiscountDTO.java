package com.amazonas.common.DiscountDTOs;

import java.util.List;

public record MultipleDiscountDTO(
                                  MultipleDiscountType multipleDiscountType,List<DiscountComponentDTO> discountComponents)
        implements DiscountComponentDTO {
}
