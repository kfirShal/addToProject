package com.amazonas.business.userProfiles;



import com.amazonas.business.inventory.Product;
import com.amazonas.utils.Pair;

import java.util.HashMap;
import java.util.Map;

public class StoreBasket {

    private final Map<String, Pair<Product, Integer>> products; // productId --> <Product,Quantity>

    public StoreBasket (){

        products = new HashMap<>();

    }

    public Pair<Product, Integer> getProductWithQuantity(String productId){

        if(!products.containsKey(productId)){
            throw new RuntimeException("Product with name: " + productId + " not found");
        }

        return products.get(productId);

    }
    public void addProduct(Product product, int quantity) {
        //TODO : store need to check if the product is legal due to policy restrictions (not for now)

        if(!isProductExists(product.productID())){
            //TODO: calculate the new price of the product if needed (not for now)
            Pair <Product, Integer> productWithQuantity = new Pair<>(product,quantity);
            products.put(product.productID(), productWithQuantity);
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

    private Boolean isProductExists(String productId) {
        return products.containsKey(productId);
    }

    public void changeProductQuantity(String productId, int quantity) {
      try{
          Pair<Product, Integer> pq = getProductWithQuantity(productId);
          pq.setSecond(quantity);

      }
      catch(Exception e){
          throw new RuntimeException(e.getMessage());
        }


    }

    public Map<String, Pair<Product, Integer>> getProducts() {
        return products;
    }

    public void mergeStoreBaskets(StoreBasket guestBasket) {
        for (Map.Entry<String, Pair<Product, Integer>> entry : guestBasket.getProducts().entrySet()) {
            String productId = entry.getKey();
            Pair<Product, Integer> guestProductWithQuantity = entry.getValue();

            Pair<Product, Integer> userProductWithQuantity = this.getProducts().get(productId);
            if (userProductWithQuantity == null) {
                // If the product ID doesn't exist in the user's basket, add the guest's product
                this.getProducts().put(productId, guestProductWithQuantity);
            } else {
                // If the product ID exists in both baskets, update the quantity
                int updatedQuantity = userProductWithQuantity.second() + guestProductWithQuantity.second();
                this.getProducts().put(productId, new Pair<>(userProductWithQuantity.first(), updatedQuantity));
            }
        }
    }
}
