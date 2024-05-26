package com.amazonas.business.stores;

import com.amazonas.business.inventory.GlobalProductTracker;
import com.amazonas.business.inventory.ProductInventory;
import org.springframework.beans.BeansException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component("storeFactory")
public class StoreFactory {

    private final GlobalProductTracker globalProductTracker;

    public StoreFactory(GlobalProductTracker globalProductTracker) {
        this.globalProductTracker = globalProductTracker;
    }

    @NonNull
    public Store getObject(String storeId, String description, Rating rating) throws BeansException {
        return new Store(storeId,
                description,
                rating,
                new ProductInventory(globalProductTracker,storeId));
    }
}
