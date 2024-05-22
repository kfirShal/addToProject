package com.amazonas.business.transactions;

import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.utils.Pair;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;

public record Transaction(
        String storeId,
        String userId,
        PaymentMethod paymentMethod,
        LocalDateTime dateOfTransaction,
        Collection<Pair<FinalProduct, Integer>> productToPrice,
        double grandTotal) {
}
