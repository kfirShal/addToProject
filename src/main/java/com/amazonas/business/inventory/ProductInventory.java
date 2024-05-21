package com.amazonas.business.inventory;

import org.springframework.security.core.parameters.P;

import java.util.HashMap;
import java.util.UUID;
import java.util.Map;

public class ProductInventory {

    private final GlobalProductTracker tracker;
    HashMap<String, Product> idToProduct;
    Map<String, Integer> idToQuantity;

    public  ProductInventory(GlobalProductTracker tracker){
        this.tracker = tracker;
        idToProduct = new HashMap<>();
        idToQuantity = new HashMap<>();
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

    public int getQuantity(Product product){
        return idToQuantity.getOrDefault(product.productID(), -1);
    }


}
