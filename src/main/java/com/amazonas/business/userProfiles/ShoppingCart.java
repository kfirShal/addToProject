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
        if(!baskets.containsKey(storeName)){
            throw new RuntimeException("Store basket with name: " + storeName + " not found");
        }
        return baskets.get(storeName);
    }

    public void addProduct(String storeName,Product product, int quantity) {
        if(baskets.containsKey(storeName)){
            StoreBasket basket = baskets.get(storeName);
            basket.addProduct(product,quantity);
        }
        else{
            StoreBasket newBasket = new StoreBasket();
            newBasket.addProduct(product,quantity);
            baskets.put(storeName,newBasket);
        }
    }

    public void removeProduct(String storeName, String productId){
        try{
            StoreBasket basket = getBasket(storeName);
            basket.removeProduct(productId);

        }
        catch(Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

    public void changeProductQuantity(String storeName, String productId,int quantity){
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

    public ShoppingCart mergeGuestCartWithRegisteredCart(ShoppingCart cartOfGuest) {


        for (Map.Entry<String, StoreBasket> entry : cartOfGuest.getBaskets().entrySet()) {
            String storeId = entry.getKey();
            StoreBasket guestBasket = entry.getValue();

            StoreBasket userBasket = this.getBaskets().get(storeId);
            if (userBasket == null) {
                // If the store ID doesn't exist in the user's cart, add the guest's basket
                this.getBaskets().put(storeId, guestBasket);

            } else {
                // If the store ID exists in both carts, merge the products
                userBasket.mergeStoreBaskets(guestBasket);
            }
        }

        return this;
    }

    public boolean isStoreExists(String storeName) {
        return baskets.containsKey(storeName);
    }
}
