package com.amazonas.business.stores.reservations;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class Reservation {

    private final String userId;
    private final String reservationId;
    private final String storeId;
    private final Map<String, Integer> productIdToQuantity;
    private final LocalDateTime expirationDate;
    private final Function<Reservation,Boolean> cancelCallback;
    private final Runnable unReserveBasket;
    private ReservationState state;
    public Reservation(
            String userId,
            String reservationId,
            String storeId,
            Map<String, Integer> productIdToQuantity,
            LocalDateTime expirationDate,
            Function<Reservation,Boolean> cancelCallback, Runnable unReserveBasket) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.storeId = storeId;
        this.productIdToQuantity = productIdToQuantity;
        this.expirationDate = expirationDate;
        this.cancelCallback = cancelCallback;
        this.unReserveBasket = unReserveBasket;
        state = ReservationState.PENDING;
    }

    public boolean isPaid() {
        return state.ordinal() >= ReservationState.PAID.ordinal();
    }

    public boolean isCancelled() {
        return state == ReservationState.CANCELLED;
    }

    public boolean isExpired() {
        return expirationDate.isBefore(LocalDateTime.now());
    }

    public ReservationState state() {
        return state;
    }

    /**
     * called only by the store or by an appropriate service
     */
    public void setPaid() {
        if(state == ReservationState.PENDING) {
            state = ReservationState.PAID;
        } else {
            throw new IllegalStateException("Reservation is not pending");
        }
    }

    /**
     * called only by the store or by an appropriate service
     */
    public void setCancelled() {
        if (state.ordinal() >= ReservationState.PAID.ordinal()) {
            throw new IllegalStateException("Reservation is already paid");
        }
        state = ReservationState.CANCELLED;
    }

    public void cancelReservation() {
        if(cancelCallback.apply(this)){
            unReserveBasket.run();
        }
    }

    public Map<String, Integer> productIdToQuantity() {
        return productIdToQuantity;
    }

    public LocalDateTime expirationDate() {
        return expirationDate;
    }

    public String storeId() {
        return storeId;
    }

    public String reservationId() {
        return reservationId;
    }

    public String userId() {
        return userId;
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
