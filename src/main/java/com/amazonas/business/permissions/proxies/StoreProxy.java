package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.stores.Rating;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.stores.StoresController;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component("storeProxy")
public class StoreProxy extends ControllerProxy{

    private final StoresController real;

    public StoreProxy(StoresController storesController, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = storesController;
    }

    public void addStorePermission(String storeId, String userId, StoreActions action, String token) throws NoPermissionException {
        if(! perm.checkPermission(storeId, userId, action)){
            throw new NoPermissionException("User does not have permission to add permission");
        }

        real.addStorePermission(storeId, userId, action, token);
    }

}
