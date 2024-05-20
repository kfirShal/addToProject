package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private Map<String,StoreBasket> baskets; // storeName --> StoreBasket

    public ShoppingCart(){
         baskets = new HashMap<>();
    }

    public StoreBasket getBasket(String storeName){
        if(baskets == null){
            throw new RuntimeException("Store Baskets has not been initialized");
        }

        if(!baskets.containsKey(storeName)){
            throw new RuntimeException("Store basket with name: " + storeName + " not found");
        }

        return baskets.get(storeName);

    }

    public void addProduct(String storeName,int productId, Product product, int quantity) {
        if(baskets.containsKey(storeName)){
            StoreBasket basket = baskets.get(storeName);
            basket.addProduct(productId,product,quantity);
        }
        else{
            StoreBasket newBasket = new StoreBasket();
            newBasket.addProduct(productId,product,quantity);
            baskets.put(storeName,newBasket);
        }

    }

    public void removeProduct(String storeName, int productId){
        try{
            StoreBasket basket = getBasket(storeName);
            basket.removeProduct(productId);

        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public Boolean isProductExists(String storeName, int productId){
        try{
            StoreBasket basket = getBasket(storeName);
            return basket.isProductExists(productId);

        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public void changeProductQuantity(String storeName, int productId,int quantity){
        try{
            StoreBasket basket = getBasket(storeName);
            basket.changeProductQuantity(productId,quantity);

        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public Map<String, StoreBasket> getBaskets() {
        return baskets;
    }


}
