package com.amazonas.business.transactions;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentMethod;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public record Transaction(
        String storeId,
        String userId,
        PaymentMethod paymentMethod,
        LocalDateTime dateOfTransaction,
        Map<Product, Integer> productToPrice) {

    public Transaction(String storeId, String userId, PaymentMethod paymentMethod, LocalDateTime dateOfTransaction, Map<Product, Integer> productToPrice) {
        this.storeId = storeId;
        this.userId = userId;
        this.paymentMethod = paymentMethod;
        this.dateOfTransaction = dateOfTransaction;
        this.productToPrice = Collections.unmodifiableMap(new HashMap<>() {{
            productToPrice.forEach((key, value) -> put(key.clone(), value));
        }});
    }
}
