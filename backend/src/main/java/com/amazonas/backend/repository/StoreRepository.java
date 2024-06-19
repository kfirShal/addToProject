package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.mongoCollections.StoreMongoCollection;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("storeRepository")
public class StoreRepository extends AbstractCachingRepository<Store> {


    private final ReadWriteLock storeLock;
    private final Map<String, Store> storeCache;


    public StoreRepository(StoreMongoCollection repo) {
        super(repo);
        storeLock = new ReadWriteLock();
        storeCache = new HashMap<>();
    }

    public void saveStore(Store store) {
        storeLock.acquireWrite();
        try {
            storeCache.put(store.getStoreId(), store);
        } finally {
            storeLock.releaseWrite();
        }
    }

    public Store getStore(String storeId) {
        storeLock.acquireRead();
        try {
            return storeCache.get(storeId);
        } finally {
            storeLock.releaseRead();
        }
    }

    public Collection<Store> getAllStores(){
        return storeCache.values();
    }

    public void saveAllStores(Collection<Store> stores) {
        storeLock.acquireWrite();
        try {
            stores.forEach(store -> storeCache.put(store.getStoreId(), store));
        } finally {
            storeLock.releaseWrite();
        }
    }

    public boolean storeNameExists(String name){

        //TODO: replace this with a database query
        storeLock.acquireRead();
        try {
            return storeCache.values().stream().anyMatch(store -> store.getStoreName().equals(name));
        } finally {
            storeLock.releaseRead();
        }
    }

}