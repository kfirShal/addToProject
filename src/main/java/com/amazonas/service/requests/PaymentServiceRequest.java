package com.amazonas.service.requests;

import com.amazonas.business.payment.PaymentService;

public record PaymentServiceRequest(String serviceId, PaymentService paymentService) {
}
