package com.amazonas.common.dtos;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class Transaction {
    private final String transactionId;
    private final String storeId;
    private final String userId;
    private final LocalDateTime dateOfTransaction;
    private final Map<Product, Integer> productToQuantity;

    private TransactionState state;

    public Transaction(String transactionId,
                       String storeId,
                       String userId,
                       LocalDateTime dateOfTransaction,
                       Map<Product, Integer> productToQuantity) {
        this.transactionId = transactionId;
        this.storeId = storeId;
        this.userId = userId;
        this.dateOfTransaction = dateOfTransaction;
        this.productToQuantity = Collections.unmodifiableMap(new HashMap<>() {{
            productToQuantity.forEach((key, value) -> put(key.clone(), value));
        }});

        this.state = TransactionState.PENDING_SHIPMENT;
    }

    public void setShipped() {
        if(this.state != TransactionState.PENDING_SHIPMENT){
            throw new IllegalStateException("Transaction is not waiting for shipment.");
        }
        this.state = TransactionState.SHIPPED;
    }

    public void setDelivered() {
        if(this.state != TransactionState.SHIPPED){
            throw new IllegalStateException("Transaction is not shipped.");
        }
        this.state = TransactionState.DELIVERED;
    }

    public void setCancelled() {
        if(state == TransactionState.CANCELED){
            throw new IllegalStateException("Transaction already canceled.");
        }
        if(this.state != TransactionState.PENDING_SHIPMENT){
            throw new IllegalStateException("Transaction is already shipped or delivered.");
        }

        this.state = TransactionState.CANCELED;
    }

    public TransactionState getState() {
        return state;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getDateOfTransaction() {
        return dateOfTransaction;
    }

    public Map<Product, Integer> getProductToQuantity() {
        return productToQuantity;
    }

    @Override
    public String toString() {
        return "Transaction[" +
                "transactionId=" + transactionId + ", " +
                "storeId=" + storeId + ", " +
                "userId=" + userId + ", " +
                "dateOfTransaction=" + dateOfTransaction + ", " +
                "productToPrice=" + productToQuantity + ']';
    }

    public TransactionState state() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(transactionId, that.transactionId) && Objects.equals(storeId, that.storeId) && Objects.equals(userId, that.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, storeId, userId);
    }
}
