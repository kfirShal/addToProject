package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.market.GlobalSearchRequest;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.exceptions.InvalidTokenException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Component("storesController")
public class StoresController {
    private final PermissionsController pc;

    public StoresController(PermissionsController pc){
        this.pc = pc;
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
