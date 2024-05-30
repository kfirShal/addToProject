package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.stores.factories.StoreFactory;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.stores.search.GlobalSearchRequest;
import com.amazonas.exceptions.InvalidTokenException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.exceptions.StoreException;
import com.amazonas.repository.RepositoryFacade;
import com.amazonas.utils.JsonUtils;
import com.amazonas.utils.Rating;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Component("storesController")
public class StoresController {
    private final RepositoryFacade repositoryFacade;
    private final StoreFactory storeFactory;

    public StoresController(RepositoryFacade repositoryFacade, StoreFactory storeFactory){
        this.repositoryFacade = repositoryFacade;
        this.storeFactory = storeFactory;
    }

    public void addStore(String ownerID,String name, String description) throws StoreException {
        if(doesNameExists(name))
            throw new StoreException("Store name already exists");
        Store toAdd = storeFactory.get(ownerID,name,description);
        repositoryFacade.saveStore(toAdd);
    }

    public void openStore(String storeId){
        getStore(storeId).openStore();
    }

    public void closeStore(String storeId){
        getStore(storeId);
    }

    //TODO: implement this
    private boolean doesNameExists(String name){
        for(Store store : repositoryFacade.getAllStores());
        return false;
    }

    public void addProduct(String storeId,Product toAdd) throws StoreException {
        getStore(storeId).addProduct(toAdd);
    }

    public void updateProduct(String storeId,Product toUpdate) throws StoreException {
        getStore(storeId).updateProduct(toUpdate);
    }

    public void removeProduct(String storeId,String productId) throws StoreException {
        getStore(storeId).removeProduct(productId);
    }
    public void disableProduct(String storeId,String productId){
        getStore(storeId).disableProduct(productId);
    }
    public void enableProduct(String storeId,String productId){
        getStore(storeId).enableProduct(productId);
    }

    public void addOwner(String username, String storeId, String logged){
        getStore(storeId).addOwner(logged,username);
    }
    public void addManager(String logged, String storeId, String username){
        getStore(storeId).addManager(logged,username);
    }
    public void removeOwner(String username,String storeId, String logged){
        getStore(storeId).removeOwner(logged,username);
    }
    public void removeManager(String logged, String storeId,String username){
        getStore(storeId).removeManager(logged,username);
    }
    public void cancelReservation(String storeId, String username){
        getStore(storeId).cancelReservation(username);
    }
    public void setReservationTimeoutSeconds(String storeId, long time){
        getStore(storeId).setReservationTimeoutSeconds(time);
    }
    public boolean addPermissionToManager(String storeId,String managerId, StoreActions actions) throws StoreException {
        return getStore(storeId).addPermissionToManager(managerId,actions);
    }
    public boolean removePermissionFromManager(String storeId,String managerId, StoreActions actions) throws StoreException {
        return getStore(storeId).removePermissionFromManager(managerId,actions);
    }
    public Store getStore(String storeId){
        return repositoryFacade.getStore(storeId);
    }

    public List<Product> searchProducts(GlobalSearchRequest request) {
        List<Product> ret = new LinkedList<>();
        for (Store store : repositoryFacade.getAllStores()) {
            if (store.getStoreRating().ordinal() >= request.getStoreRating().ordinal()) {
                ret.addAll(store.searchProduct(request));
            }
        }
        return ret;
    }
}
