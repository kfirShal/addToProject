package com.amazonas.backend.business.stores.discountPolicies.DiscountDTOs;

import java.util.List;

public record MultipleDiscountDTO(List<DiscountComponentDTO> discountComponents,
                                  MultipleDiscountType multipleDiscountType)
        implements DiscountComponentDTO {
}
