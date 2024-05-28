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
    

}
