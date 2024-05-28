package com.amazonas.business.stores;

import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;

import java.util.Collection;

public interface StoresController {

    Store getStore(String storeId, String userId, String token) throws NoPermissionException, AuthenticationFailedException;
    Collection<Store> getAllStores(String userId, String token) throws NoPermissionException, AuthenticationFailedException;
    void addStore(String storeId, String storeDescription, Rating storeRating, String userId, String token) throws NoPermissionException, AuthenticationFailedException;
    void addStorePermission(String storeId, String userId, StoreActions action, String token) throws NoPermissionException, AuthenticationFailedException;
    void removeStorePermission(String storeId, String userId, StoreActions action, String token) throws NoPermissionException, AuthenticationFailedException;
}
