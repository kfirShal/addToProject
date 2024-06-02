package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.stores.reservations.Reservation;

import java.util.HashMap;
import java.util.Map;

public class ShoppingCart {

    private final StoreBasketFactory storeBasketFactory;
    private final String userId;
    private Map<String,StoreBasket> baskets; // storeName --> StoreBasket

    public ShoppingCart(StoreBasketFactory storeBasketFactory, String userId){
        this.storeBasketFactory = storeBasketFactory;
        this.userId = userId;
        baskets = new HashMap<>();
    }

    public StoreBasket getBasket(String storeName){
        if(!baskets.containsKey(storeName)){
            throw new RuntimeException("Store basket with name: " + storeName + " not found");
        }
        return baskets.get(storeName);
    }

    public void addProduct(String storeName,Product product, int quantity) {
        StoreBasket basket = baskets.computeIfAbsent(storeName, _ -> storeBasketFactory.get(storeName,userId));
        basket.addProduct(product,quantity);
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

    public Map<String, Reservation> reserveCart(){
        Map<String, Reservation> reservations = new HashMap<>();
        for(var entry : baskets.entrySet()){
            Reservation r = entry.getValue().reserveBasket();
            reservations.put(entry.getKey(),r);
        }
        return reservations;
    }

    public double getTotalPrice() {
        double totalPrice = 0;
        for (var entry : baskets.entrySet()) {
            totalPrice += entry.getValue().getTotalPrice();
        }
        return totalPrice;
    }
}
