package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.Function;

public class Reservation {
    private final String userId;
    private final Map<Product, Integer> productToQuantity;
    private final LocalDateTime expirationDate;
    private final Runnable cancelCallback;

    private boolean isPaid;

    private boolean isCancelled;
    private boolean isShipped;

    public Reservation(
            String userId,
            Map<Product, Integer> productToQuantity,
            LocalDateTime expirationDate, Runnable cancelCallback
    ) {
        this.userId = userId;
        this.productToQuantity = productToQuantity;
        this.expirationDate = expirationDate;
        this.cancelCallback = cancelCallback;
        this.isPaid = false;
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

    public boolean isShipped() {
        return isShipped;
    }

    public void setShipped() {
        isShipped = true;
    }

    public void cancelReservation() {
        isCancelled = true;
        cancelCallback.run();
    }

    public boolean isCancelled() {
        return isCancelled;
    }
}
