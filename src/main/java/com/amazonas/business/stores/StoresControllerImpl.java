package com.amazonas.business.stores;

import org.springframework.stereotype.Component;

import java.util.List;

@Component("storesController")
public class StoresControllerImpl implements StoresController {
    @Override
    public Store getStore(int storeID) {
        return null;
    }

    @Override
    public List<Store> getAllStores() {
        return null;
    }
}
