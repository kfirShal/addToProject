package com.amazonas.business.transactions;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.utils.Pair;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public record Transaction(
        String transactionId,
        String storeId,
        String userId,
        LocalDateTime dateOfTransaction,
        Map<Product, Integer> productToPrice) {

    public Transaction(String transactionId,
                       String storeId,
                       String userId,
                       LocalDateTime dateOfTransaction,
                       Map<Product, Integer> productToPrice) {
        this.transactionId = transactionId;
        this.storeId = storeId;
        this.userId = userId;
        this.dateOfTransaction = dateOfTransaction;
        this.productToPrice = Collections.unmodifiableMap(new HashMap<>() {{
            productToPrice.forEach((key, value) -> put(key.clone(), value));
        }});
    }
}
