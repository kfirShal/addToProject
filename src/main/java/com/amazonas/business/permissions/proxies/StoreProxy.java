package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.stores.Rating;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.stores.StoresController;
import com.amazonas.exceptions.NoPermissionException;
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
    public Store getStore(String storeID) {
        return null;
    }

    @Override
    public List<Store> getAllStores() {
        return null;
    }

    @Override
    public void addStore(String storeId, String storeDescription, Rating storeRating, String userId, String token) {

    }


    public void addStorePermission(String storeId, String userId, StoreActions action, String token) throws NoPermissionException {
        if(! perm.checkPermission(storeId, userId, action)){
            throw new NoPermissionException("User does not have permission to add permission");
        }

        real.addStorePermission(storeId, userId, action, token);
    }
}
