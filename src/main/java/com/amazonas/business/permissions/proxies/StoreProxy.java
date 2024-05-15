package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.stores.StoresController;
import org.springframework.stereotype.Component;

@Component
public class StoreProxy extends ControllerProxy implements StoresController {

    private final StoresController real;

    public StoreProxy(StoresController storesController, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = storesController;
    }
}
