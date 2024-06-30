package com.amazonas.backend.business.payment;

import com.amazonas.common.utils.JsonUtils;

public record PaymentRequest(String serviceId, PaymentMethod paymentMethod, double amount) {
    public static PaymentRequest from(String json) {
        return JsonUtils.deserialize(json, PaymentRequest.class);
    }
}
