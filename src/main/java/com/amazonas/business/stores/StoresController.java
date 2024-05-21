package com.amazonas.business.stores;

import java.util.List;

public interface StoresController {

    Store getStore(int storeID);
    List<Store> getAllStores();
}
