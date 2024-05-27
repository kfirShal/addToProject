package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component("reservationFactory")
public class ReservationFactory {

    private final StoreCallbackFactory storeCallbackFactory;

    public ReservationFactory(StoreCallbackFactory storeCallbackFactory) {
        this.storeCallbackFactory = storeCallbackFactory;
    }

    public Reservation get(String storeId,
                           String userId,
                           Map<Product, Integer> productToQuantity,
                           LocalDateTime expirationDate){
        return new Reservation(userId,
                productToQuantity,
                expirationDate,
                storeCallbackFactory.cancelReservation(storeId, userId));
    }
}
