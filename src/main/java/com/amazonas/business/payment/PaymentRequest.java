package com.amazonas.business.payment;

public record PaymentRequest(String serviceId,PaymentMethod paymentMethod, double amount) {
}
