package com.amazonas.business.transactions;

import com.amazonas.business.payment.PaymentMethod;

import java.time.LocalDateTime;
import java.util.Map;

public record Transaction(
        String storeId,
        String userId,
        PaymentMethod paymentMethod,
        LocalDateTime dateOfTransaction,
        Map<FinalProduct, Integer> productToPrice,
        int grandTotal) {
}
