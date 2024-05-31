package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.stores.search.GlobalSearchRequest;
import com.amazonas.exceptions.InvalidTokenException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.repository.RepositoryFacade;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component("storesController")
public class StoresController {
    private final PermissionsController pc;
    private final RepositoryFacade repositoryFacade;

    public StoresController(PermissionsController pc, RepositoryFacade repositoryFacade){
        this.pc = pc;
        this.repositoryFacade = repositoryFacade;
    }

    public boolean addOwner(String username, String storeId){
        return false;
    }

    public Store getStore(String storeID) {
        return null;
    }

    public List<Store> getAllStores() {
        return null;
    }

    public Store getStore(String storeId, String userID, String token) throws InvalidTokenException, NoPermissionException {
        return null;
    }

    public List<Store> getAllStores(String userID, String token) throws InvalidTokenException, NoPermissionException {
        return null;
    }

    public List<Product> searchProducts(GlobalSearchRequest request) {
        List<Product> ret = new LinkedList<>();
        for (Store store : getAllStores()) {
            if (store.getStoreRating().ordinal() >= request.getStoreRating().ordinal()) {
                ret.addAll(store.searchProduct(request));
            }
        }
        return ret;
    }
}
