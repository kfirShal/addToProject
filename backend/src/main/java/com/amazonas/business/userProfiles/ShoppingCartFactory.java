package com.amazonas.business.userProfiles;

import org.springframework.stereotype.Component;

@Component("shoppingCartFactory")
public class ShoppingCartFactory {


    private final StoreBasketFactory storeBasketFactory;

    public ShoppingCartFactory(StoreBasketFactory storeBasketFactory) {
        this.storeBasketFactory = storeBasketFactory;
    }

    public ShoppingCart get(String userId){
            return new ShoppingCart(storeBasketFactory,userId);
        }
}
