package com.amazonas.backend.business.stores;

import com.amazonas.backend.business.inventory.Product;
import com.amazonas.backend.business.permissions.actions.StoreActions;
import com.amazonas.backend.business.stores.factories.StoreFactory;
import com.amazonas.backend.business.stores.search.GlobalSearchRequest;
import com.amazonas.backend.business.stores.search.SearchRequest;
import com.amazonas.backend.business.stores.storePositions.StorePosition;
import com.amazonas.backend.business.transactions.Transaction;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.backend.repository.StoreRepository;
import com.amazonas.backend.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

@Component("storesController")
public class StoresController {
    private final StoreFactory storeFactory;
    private final StoreRepository repository;
    private final TransactionRepository transactionRepository;

    public StoresController(StoreFactory storeFactory, StoreRepository storeRepository, TransactionRepository transactionRepository){
        this.storeFactory = storeFactory;
        this.repository = storeRepository;
        this.transactionRepository = transactionRepository;
    }

    public void addStore(String ownerID,String name, String description) throws StoreException {
        if(doesNameExists(name))
            throw new StoreException("Store name already exists");
        Store toAdd = storeFactory.get(ownerID,name,description);
        repository.saveStore(toAdd);
    }

    public boolean openStore(String storeId){
        return getStore(storeId).openStore();
    }

    public boolean closeStore(String storeId){
        return getStore(storeId).closeStore();
    }

    private boolean doesNameExists(String name){
        //TODO: replace this with a database query
        return repository.storeNameExists(name);
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

    public boolean addPermissionToManager(String storeId,String managerId, StoreActions actions) throws StoreException {
        return getStore(storeId).addPermissionToManager(managerId,actions);
    }

    public boolean removePermissionFromManager(String storeId,String managerId, StoreActions actions) throws StoreException {
        return getStore(storeId).removePermissionFromManager(managerId,actions);
    }

    public Store getStore(String storeId){
        return repository.getStore(storeId);
    }

    public List<Product> searchProductsGlobally(GlobalSearchRequest request) {
        List<Product> ret = new LinkedList<>();
        for (Store store : repository.getAllStores()) {
            if (store.getStoreRating().ordinal() >= request.getStoreRating().ordinal()) {
                ret.addAll(store.searchProduct(request));
            }
        }
        return ret;
    }

    public List<Product> searchProductsInStore(String storeId, SearchRequest request) {
        return getStore(storeId).searchProduct(request);
    }

    public List<StorePosition> getStoreRolesInformation(String storeId) {
        return getStore(storeId).getRolesInformation();
    }

    public List<Transaction> getStoreTransactionHistory(String storeId) {
        return transactionRepository.getTransactionHistoryByStore(storeId);
    }
}
