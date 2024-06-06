package com.amazonas.business.userProfiles;



import com.amazonas.business.inventory.Product;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.utils.Pair;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class StoreBasket {

    private final Map<String, Pair<String, Integer>> products; // productId --> <Product,Quantity>

    private final Function<Map<String,Integer>, Reservation> makeReservation;
    private final Function<Map<String, Integer>, Double> calculatePrice;

    public StoreBasket (Function<Map<String,Integer>,
                        Reservation> makeReservation,
                        Function<Map<String,Integer>,Double> calculatePrice){

        this.makeReservation = makeReservation;
        this.calculatePrice = calculatePrice;
        products = new HashMap<>();
    }

    public Pair<String, Integer> getProductWithQuantity(String productId){

        if(!products.containsKey(productId)){
            throw new RuntimeException("Product with name: " + productId + " not found");
        }

        return products.get(productId);

    }

    public void addProduct(String productId, int quantity) {
        //TODO : store need to check if the product is legal due to policy restrictions (not for now)

        if(!isProductExists(productId)){
            //TODO: calculate the new price of the product if needed (not for now)
            Pair <String, Integer> productWithQuantity = new Pair<>(productId,quantity);
            products.put(productId, productWithQuantity);
        }
        else{
            throw new RuntimeException("Product is already exists, change the quantity of the product if needed");
        }
    }
    public void removeProduct(String productId) {
        if(!products.containsKey(productId)){
            throw new RuntimeException("Product with id: " + productId + " not found");
        }

        products.remove(productId);
    }

    Boolean isProductExists(String productId) {
        return products.containsKey(productId);
    }

    public void changeProductQuantity(String productId, int quantity) {
      try{
          Pair<String, Integer> pq = getProductWithQuantity(productId);
          pq.setSecond(quantity);

      }
      catch(Exception e) {
          throw new RuntimeException(e.getMessage());
      }
    }


    public Map<String, Pair<String, Integer>> getProducts() {
        return products;
    }

    public Map<String,Integer> getProductsMap() {
        return new HashMap<>() {{
            for (var entry : products.entrySet()) {
                var pair = entry.getValue();
                put(pair.first(), pair.second());
            }
        }};
    }

    public void mergeStoreBaskets(StoreBasket guestBasket) {
        for (Map.Entry<String, Pair<String, Integer>> entry : guestBasket.products.entrySet()) {
            String productId = entry.getKey();
            Pair<String, Integer> guestProductWithQuantity = entry.getValue();

            Pair<String, Integer> userProductWithQuantity = this.products.get(productId);
            if (userProductWithQuantity == null) {
                // If the product ID doesn't exist in the user's basket, add the guest's product
                this.products.put(productId, guestProductWithQuantity);
            } else {
                // If the product ID exists in both baskets, update the quantity
                int updatedQuantity = userProductWithQuantity.second() + guestProductWithQuantity.second();
                this.products.put(productId, Pair.of(userProductWithQuantity.first(), updatedQuantity));
            }
        }
    }

    public Reservation reserveBasket() {
        return makeReservation.apply(getProductsMap());
    }

    public double getTotalPrice() {
        return calculatePrice.apply(getProductsMap());
    }
}
