package com.amazonas.common.dtos;

import com.amazonas.common.utils.ReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {
    private final Map<String,StoreBasket> baskets; // storeId -> StoreBasket
    private static final Logger log = LoggerFactory.getLogger(ShoppingCart.class);
    private StoreBasketFactory storeBasketFactory;
    private String userId;
    private ReadWriteLock lock;

    public ShoppingCart(StoreBasketFactory storeBasketFactory, String userId) {
        this.storeBasketFactory = storeBasketFactory;
        this.userId = userId;
        baskets = new HashMap<>();
        lock = new ReadWriteLock();
    }
    public ShoppingCart(Map<String, StoreBasket> baskets) {
        this.baskets = baskets;
    }

    public Map<String, StoreBasket> getBaskets() {
        return baskets;
    }

    public StoreBasket getBasket(String storeId) {
        return baskets.get(storeId);
    }

    public void addBasket(String storeId, StoreBasket basket) {
        baskets.put(storeId, basket);
    }

    public void removeBasket(String storeId) {
        baskets.remove(storeId);
    }

    public void clear() {
        baskets.clear();
    }


}
