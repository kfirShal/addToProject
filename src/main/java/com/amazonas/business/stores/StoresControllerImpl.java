package com.amazonas.business.stores;

import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.exceptions.InvalidTokenException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("storesController")
public class StoresControllerImpl implements StoresController {
    private final PermissionsController pc;

    public StoresControllerImpl(PermissionsController pc){
        this.pc = pc;
    }

    public boolean addOwner(String username, int storeID){
        return false;
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
    public Store getStore(int storeID, String userID, String token) throws InvalidTokenException, NoPermissionException {
        return null;
    }

    @Override
    public List<Store> getAllStores(String userID, String token) throws InvalidTokenException, NoPermissionException {
        return null;
    }
}
