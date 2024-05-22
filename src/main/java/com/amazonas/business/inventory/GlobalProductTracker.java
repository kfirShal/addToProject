package com.amazonas.business.inventory;

import com.amazonas.business.stores.Store;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class GlobalProductTracker {

    private ConcurrentMap<Product, Store> productToStore;

    public GlobalProductTracker(){
        productToStore = new ConcurrentHashMap<>();
    }

    public boolean productExists(Product product){
        return productToStore.containsKey(product);
    }

    public boolean addProduct(Product product, Store store){
        if(productExists(product)){
            return false;
        }
        productToStore.put(product,store);
        return true;
    }

}
