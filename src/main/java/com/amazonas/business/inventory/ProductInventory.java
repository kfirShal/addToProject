package com.amazonas.business.inventory;

import java.util.HashMap;

public class ProductInventory {

    HashMap<String, Product> hashMap;

    public  ProductInventory(){
        hashMap = new HashMap<>();
    }

    public void addProduct(Product product) {
        if(!hashMap.containsKey(product.productID()))
            hashMap.put(product.productID(), product);
        else System.out.println("This key already exist");
    }

    public void removeProduct(Product product) {
        hashMap.remove(product.productID());
    }




    public void increaseQuantity(Product product, int quantity) {
    }

    public void decreaseQuantity(Product product, int quantity) {
    }

}
