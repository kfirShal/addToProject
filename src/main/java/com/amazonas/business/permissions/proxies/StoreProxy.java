package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.stores.Rating;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoresController;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("storeProxy")
public class StoreProxy extends ControllerProxy implements StoresController {

    private final StoresController real;

    public StoreProxy(StoresController storesController, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = storesController;
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

    }
}
