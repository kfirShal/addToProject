package com.amazonas.business.transactions;

import com.amazonas.business.inventory.Product;

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

    public String transactionId() {
        return transactionId;
    }

    public String storeId() {
        return storeId;
    }

    public String userId() {
        return userId;
    }

    public LocalDateTime dateOfTransaction() {
        return dateOfTransaction;
    }

    public Map<Product, Integer> productToQuantity() {
        return productToQuantity;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Transaction) obj;
        return Objects.equals(this.transactionId, that.transactionId) &&
                Objects.equals(this.storeId, that.storeId) &&
                Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.dateOfTransaction, that.dateOfTransaction) &&
                Objects.equals(this.productToQuantity, that.productToQuantity);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId, storeId, userId, dateOfTransaction, productToQuantity);
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
}
