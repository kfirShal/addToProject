package com.amazonas.business.inventory;

import com.amazonas.business.stores.Store;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class GlobalProductTracker {
    Map<Product, Store> productToStore;

    public GlobalProductTracker(){
        productToStore = new HashMap<>();
    }

    public boolean productExists(Product product){
        return productToStore.containsKey(product);
    }

    public void addProduct(Product toAdd, Store store){
        productToStore.put(toAdd, store);
    }
}
