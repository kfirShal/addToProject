package com.amazonas.business.inventory;

import org.springframework.security.core.parameters.P;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ProductInventory {

    private final GlobalProductTracker tracker;
    ConcurrentMap<String, Product> idToProduct;
    ConcurrentMap<String, Integer> idToQuantity;

    public  ProductInventory(GlobalProductTracker tracker){
        this.tracker = tracker;
        idToProduct = new ConcurrentHashMap<>();
        idToQuantity = new ConcurrentHashMap<>();
    }

    public void addProduct(Product product) {
        String newId;
        do{
            newId = UUID.randomUUID().toString();
            product.changeProductID(newId);
        }while (tracker.productExists(product));
        idToProduct.put(product.productID(),product);
    }

    public void removeProduct(Product product) {
        idToProduct.remove(product.productID());
    }

    public void updateProduct(Product product){

        // we want to make sure the object is the same object
        // so we can update it for the entire system
        if(idToProduct.containsKey(product.productID())) {
            Product product1 = idToProduct.get(product.productID());
            product1.changeNameProduct(product.nameProduct());
            product1.changeCategory(product.category());
            product1.changeRate(product.rate());
            product1.changePrice(product.price());
            product1.changeDescription(product.description());
        }
    }

    public void setQuantity(Product product, int quantity) {
        idToQuantity.put(product.productID(), quantity);
    }

    /**
     * @return the quantity of the product. -1 if the product is not in the inventory
     */
    public int getQuantity(Product product){
        return idToQuantity.getOrDefault(product.productID(), -1);
    }

}
