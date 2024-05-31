package com.amazonas.business.userProfiles;

import com.amazonas.business.stores.factories.StoreCallbackFactory;
import org.springframework.stereotype.Component;

@Component("storeBasketFactory")
public class StoreBasketFactory {


    private final StoreCallbackFactory storeCallbackFactory;

    public StoreBasketFactory(StoreCallbackFactory storeCallbackFactory) {
        this.storeCallbackFactory = storeCallbackFactory;
    }

    public StoreBasket get(String storeId, String userId){
        return new StoreBasket(storeCallbackFactory.makeReservation(storeId, userId),
                                storeCallbackFactory.cancelReservation(storeId, userId), storeCallbackFactory.calculatePrice(storeId));
    }
}
