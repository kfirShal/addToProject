package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class Reservation {
    private final String userId;
    private final Map<Product, Integer> productToQuantity;
    private final LocalDateTime expirationDate;

    private boolean isPaid;

    public Reservation(
            String userId,
            Map<Product, Integer> productToQuantity,
            LocalDateTime expirationDate,
            boolean isPaid
    ) {
        this.userId = userId;
        this.productToQuantity = productToQuantity;
        this.expirationDate = expirationDate;
        this.isPaid = isPaid;
    }

    public String userId() {
        return userId;
    }

    public Map<Product, Integer> productToQuantity() {
        return productToQuantity;
    }

    public LocalDateTime expirationDate() {
        return expirationDate;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid() {
        isPaid = true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Reservation) obj;
        return Objects.equals(this.userId, that.userId) &&
                Objects.equals(this.productToQuantity, that.productToQuantity) &&
                Objects.equals(this.expirationDate, that.expirationDate) &&
                this.isPaid == that.isPaid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, productToQuantity, expirationDate, isPaid);
    }

    @Override
    public String toString() {
        return "Reservation[" +
                "userId=" + userId + ", " +
                "productToQuantity=" + productToQuantity + ", " +
                "expirationDate=" + expirationDate + ", " +
                "isPaid=" + isPaid + ']';
    }


}
