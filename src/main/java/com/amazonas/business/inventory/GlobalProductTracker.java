package com.amazonas.business.inventory;

import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component
public class GlobalProductTracker {

    private final ConcurrentMap<String, String> productToStore;

    public GlobalProductTracker(){
        productToStore = new ConcurrentHashMap<>();
    }

    public boolean productExists(String productId){
        return productToStore.containsKey(productId);
    }

    public void addProduct(String productId, String storeId){
        if(productExists(productId)){
            return;
        }
        productToStore.put(productId,storeId);
    }

}
