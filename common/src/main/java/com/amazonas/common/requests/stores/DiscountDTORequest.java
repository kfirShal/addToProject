package com.amazonas.common.requests.stores;

import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;

public record DiscountDTORequest(String StoreID, DiscountComponentDTO discountComponentDTO) {
}
