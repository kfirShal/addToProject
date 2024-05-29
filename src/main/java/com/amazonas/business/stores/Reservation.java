package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;

public class Reservation {

    private final String reservationId;
    private final String userId;
    private final Map<Product, Integer> productToQuantity;
    private final LocalDateTime expirationDate;
    private final Runnable cancelCallback;
    private ReservationState state;
    public Reservation(
            String reservationId,
            String userId,
            Map<Product, Integer> productToQuantity,
            LocalDateTime expirationDate, Runnable cancelCallback
    ) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.productToQuantity = productToQuantity;
        this.expirationDate = expirationDate;
        this.cancelCallback = cancelCallback;
        state = ReservationState.PENDING;
    }

    public boolean isPaid() {
        return state.ordinal() >= ReservationState.PAID.ordinal();
    }

    public boolean isShipped() {
        return state == ReservationState.SHIPPED;
    }

    public boolean isCancelled() {
        return state == ReservationState.CANCELLED;
    }

    public ReservationState state() {
        return state;
    }

    public void setPaid() {
        if(state == ReservationState.PENDING) {
            state = ReservationState.PAID;
        } else {
            throw new IllegalStateException("Reservation is not pending");
        }
    }

    public void setShipped() {
        if(state == ReservationState.PAID) {
            state = ReservationState.SHIPPED;
        } else {
            throw new IllegalStateException("Reservation is not paid");
        }
    }

    public void cancelReservation() {
        if(state.ordinal() >= ReservationState.PAID.ordinal()) {
            throw new IllegalStateException("Reservation is already paid");
        }
        state = ReservationState.CANCELLED;
        cancelCallback.run();
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

    public String reservationId() {
        return reservationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reservation that = (Reservation) o;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(reservationId);
    }
}
