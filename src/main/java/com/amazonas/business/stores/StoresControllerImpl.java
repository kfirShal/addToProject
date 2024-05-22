package com.amazonas.business.stores;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("storesController")
public class StoresControllerImpl implements StoresController {


    private final StoreFactory storeProvider;
    private final ConcurrentMap<String,Store> storeIdToStore;


    public StoresControllerImpl(StoreFactory storeProvider) {
        this.storeProvider = storeProvider;
        storeIdToStore = new ConcurrentHashMap<>();
    }

    @Override
    public Store getStore(int storeID) {
        return null;
    }

    @Override
    public List<Store> getAllStores() {
        return null;
    }

    @Override
    public void addStore(String storeId, String storeDescription, Rating storeRating) {
        if(storeIdToStore.containsKey(storeId)){
            throw new IllegalArgumentException("Store with id " + storeId + " already exists");
        }
        Store toAdd = storeProvider.getObject(storeId,storeDescription,storeRating);
        storeIdToStore.put(toAdd.getStoreId(),toAdd);
    }
}
