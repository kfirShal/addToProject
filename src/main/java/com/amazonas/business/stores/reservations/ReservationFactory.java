package com.amazonas.business.stores.reservations;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.stores.factories.StoreCallbackFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component("reservationFactory")
public class ReservationFactory {

    private final StoreCallbackFactory storeCallbackFactory;

    public ReservationFactory(StoreCallbackFactory storeCallbackFactory) {
        this.storeCallbackFactory = storeCallbackFactory;
        loadIdGenerator();
    }

    //TODO: load id generator from database
    private void loadIdGenerator(){

    }

    public Reservation get(String storeId,
                           String userId,
                           Map<Product, Integer> productToQuantity,
                           LocalDateTime expirationDate){
        return new Reservation(
                UUID.randomUUID().toString(),
                userId,
                productToQuantity,
                expirationDate,
                storeCallbackFactory.cancelReservation(storeId, userId));
    }
}
