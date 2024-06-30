package com.amazonas.common.requests.shipping;

import com.amazonas.common.utils.JsonUtils;

public record ShipmentRequest(String transactionId, String serviceId, String storeId) {
    public static ShipmentRequest from(String json) {
        return JsonUtils.deserialize(json, ShipmentRequest.class);
    }
}
