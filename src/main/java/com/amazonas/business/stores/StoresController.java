package com.amazonas.business.stores;

import com.amazonas.exceptions.InvalidTokenException;
import com.amazonas.exceptions.NoPermissionException;

import java.util.List;

public interface StoresController {

    Store getStore(int storeID);

    List<Store> getAllStores();

    Store getStore(int storeID, String userID, String token) throws InvalidTokenException, NoPermissionException;
    List<Store> getAllStores(String userID, String token) throws InvalidTokenException, NoPermissionException;
}
