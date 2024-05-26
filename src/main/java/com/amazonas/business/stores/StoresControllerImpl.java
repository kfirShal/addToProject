package com.amazonas.business.stores;

import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("storesController")
public class StoresControllerImpl implements StoresController {


    private final StoreFactory storeFactory;
    private final ConcurrentMap<String,Store> storeIdToStore;

    public StoresControllerImpl(StoreFactory storeFactory) {
        this.storeFactory = storeFactory;
        storeIdToStore = new ConcurrentHashMap<>();
    }

    @Override
    public Store getStore(String storeId) {
        return null;
    }

    @Override
    public Collection<Store> getAllStores() {
        return storeIdToStore.values();
    }

    @Override
    public void addStore(String storeId, String storeDescription, Rating storeRating) {
        if(storeIdToStore.containsKey(storeId)){
            throw new IllegalArgumentException("Store with id " + storeId + " already exists");
        }
        Store toAdd = storeFactory.getObject(storeId,storeDescription,storeRating);
        storeIdToStore.put(toAdd.getStoreId(),toAdd);
    }
}
