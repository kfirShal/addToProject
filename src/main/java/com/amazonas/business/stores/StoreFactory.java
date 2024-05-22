package com.amazonas.business.stores;

import com.amazonas.business.inventory.GlobalProductTracker;
import com.amazonas.business.inventory.ProductInventory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component("storeProvider")
public class StoreFactory implements ObjectProvider<Store> {

    private final GlobalProductTracker globalProductTracker;

    public StoreFactory(GlobalProductTracker globalProductTracker) {
        this.globalProductTracker = globalProductTracker;
    }

    /**
     * @return a new Store object without storeId, storeDescription, and storeRating
     */
    @Override
    @NonNull
    public Store getObject() throws BeansException {
        return new Store(globalProductTracker, new ProductInventory(globalProductTracker));
    }

    /**
     * @param args {storeId, storeDescription, storeRating}
     */
    @Override
    @NonNull
    public Store getObject(Object... args) throws BeansException {
        Store toReturn = this.getObject();
        toReturn.setStoreId((String) args[0]);
        toReturn.setStoreDescription((String) args[1]);
        toReturn.setStoreRating((Rating) args[2]);
        return toReturn;
    }

    /**
     * not supported
     */
    @Override
    public Store getIfAvailable() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported");
    }

    /**
     * not supported
     */
    @Override
    public Store getIfUnique() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Not supported");
    }
}
