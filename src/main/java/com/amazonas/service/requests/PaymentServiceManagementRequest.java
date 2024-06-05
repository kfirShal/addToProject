package com.amazonas.service.requests;

import com.amazonas.business.payment.PaymentService;

public record PaymentServiceManagementRequest(String serviceId, PaymentService paymentService) {
}
