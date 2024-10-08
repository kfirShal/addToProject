package com.amazonas.backend.business.userProfiles;

import com.amazonas.backend.business.stores.factories.StoreCallbackFactory;
import org.springframework.stereotype.Component;

@Component("storeBasketFactory")
public class StoreBasketFactory {


    private final StoreCallbackFactory storeCallbackFactory;

    public StoreBasketFactory(StoreCallbackFactory storeCallbackFactory) {
        this.storeCallbackFactory = storeCallbackFactory;
    }

    public StoreBasket get(String storeId, String userId){
        return new StoreBasket(storeCallbackFactory.makeReservation(storeId, userId),
                                storeCallbackFactory.calculatePrice(storeId));
    }
}
