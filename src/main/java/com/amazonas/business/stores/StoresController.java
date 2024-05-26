package com.amazonas.business.stores;

import java.util.Collection;

public interface StoresController {

    Store getStore(String storeID);
    Collection<Store> getAllStores();
    void addStore(String storeId, String storeDescription, Rating storeRating);
}
