package com.amazonas.business.stores;

import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("storesController")
public class StoresControllerImpl implements StoresController {

    private final StoreFactory storeFactory;
    private final ConcurrentMap<String,Store> storeIdToStore;
    private final PermissionsController permissionsController;

    public StoresControllerImpl(StoreFactory storeFactory, PermissionsController permissionsController) {
        this.storeFactory = storeFactory;
        storeIdToStore = new ConcurrentHashMap<>();
        this.permissionsController = permissionsController;
    }

    @Override
    public Store getStore(String storeId, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        return null;
    }

    @Override
    public Collection<Store> getAllStores(String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        return List.of();
    }

    @Override
    public void addStore(String storeId, String storeDescription, Rating storeRating, String userId, String token) {
        if(storeIdToStore.containsKey(storeId)){
            throw new IllegalArgumentException("Store with id " + storeId + " already exists");
        }
        Store toAdd = storeFactory.getObject(storeId,storeDescription,storeRating);
        storeIdToStore.put(toAdd.getStoreId(),toAdd);
    }

    //TODO: TAMIR - implement this
    public String createStore(){
        return null;
    }

    //TODO: TAMIR - implement this
    public String appointOwner(){
        return null;
    }

    //TODO: TAMIR - implement this
    public String removeOwner(){
        return null;
    }

    //TODO: TAMIR - implement this
    public String appointManager(){
        return null;
    }

    //TODO: TAMIR - implement this
    public String removeManager(){
        return null;
    }

    //TODO: SHOHAM - implement this
    @Override
    public void addStorePermission(String storeId, String userId, StoreActions action, String token)  {

    }

    //TODO: SHOHAM - implement this
    public void removeStorePermission(String storeId, String userId, StoreActions action, String token) {

    }

    //TODO: SHOHAM - implement this
    public String closeStore(){
        return null;
    }

    //TODO: SHOHAM - implement this
    public String openStore(){
        return null;
    }

    public String getStorePositions(){
        return null;
    }
}
