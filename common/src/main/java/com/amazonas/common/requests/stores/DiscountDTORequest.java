package com.amazonas.common.requests.stores;

import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.common.utils.JsonUtils;

public record DiscountDTORequest(String StoreID, DiscountComponentDTO discountComponentDTO) {
    public static DiscountDTORequest from(String payload) {
        return JsonUtils.deserialize(payload,DiscountDTORequest.class);
    }
}
