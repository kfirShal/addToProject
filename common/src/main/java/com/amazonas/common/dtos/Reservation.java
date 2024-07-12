package com.amazonas.common.dtos;

import java.time.LocalDateTime;
import java.util.Map;
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

    public Reservation(String userId, String reservationId, String storeId, Map<String, Integer> productIdToQuantity, LocalDateTime expirationDate, Function<Reservation, Boolean> cancelCallback, Runnable unReserveBasket) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.storeId = storeId;
        this.productIdToQuantity = productIdToQuantity;
        this.expirationDate = expirationDate;
        this.cancelCallback = cancelCallback;
        this.unReserveBasket = unReserveBasket;
    }

    // getters
    public Map<String, Integer> getProductIdToQuantity() {
        return productIdToQuantity;
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

    // make reservation
    public void setPaid() {
        if (state == ReservationState.PENDING) {
            state = ReservationState.PAID;
        } else {
            throw new IllegalStateException("Reservation is not pending");
        }
    }

    public boolean cancelReservation() {
        if(state == ReservationState.PENDING){
            if(cancelCallback.apply(this)){
                unReserveBasket.run();
                return true;
            }
        }
        return false;
    }

    public void setCancelled() {
        if (state.ordinal() >= ReservationState.PAID.ordinal()) {
            throw new IllegalStateException("Reservation is already paid");
        }
        state = ReservationState.CANCELLED;
    }



    public enum ReservationState {
        PENDING,
        CANCELLED,
        PAID
    }

}
