package com.amazonas.backend.service.requests.payment;

import com.amazonas.backend.business.payment.PaymentService;

public record PaymentServiceManagementRequest(String serviceId, PaymentService paymentService) {
}
