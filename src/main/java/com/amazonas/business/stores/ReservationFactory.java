package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.utils.IdGenerator;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

@Component("reservationFactory")
public class ReservationFactory {

    private final StoreCallbackFactory storeCallbackFactory;
    private final IdGenerator idGenerator;

    public ReservationFactory(StoreCallbackFactory storeCallbackFactory) {
        this.storeCallbackFactory = storeCallbackFactory;
        idGenerator = new IdGenerator();
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
                idGenerator.nextId(),
                userId,
                productToQuantity,
                expirationDate,
                storeCallbackFactory.cancelReservation(storeId, userId));
    }
}
