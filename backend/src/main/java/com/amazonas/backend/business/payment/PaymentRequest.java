package com.amazonas.backend.business.payment;

public record PaymentRequest(String serviceId,PaymentMethod paymentMethod, double amount) {
}
