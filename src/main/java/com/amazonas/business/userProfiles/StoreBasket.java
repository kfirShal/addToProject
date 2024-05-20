package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductWithQuantity;

import java.util.HashMap;
import java.util.Map;

public class StoreBasket {

    private Map<Integer, ProductWithQuantity> products; // productId --> <Product,Quantity>

    public StoreBasket (){

        products = new HashMap<>();

    }

    public ProductWithQuantity getProduct(int productId){
        if(products == null){
            throw new RuntimeException("Products have not been initialized");
        }

        if(!products.containsKey(productId)){
            throw new RuntimeException("Product with name: " + productId + " not found");
        }

        return products.get(productId);

    }
    public void addProduct(int productId, Product product, int quantity) {
        if(products == null){
            throw new RuntimeException("Store Baskets has not been initialized");
        }

        //TODO : store need to check if the product is legal due to policy restrictions (not for now)

        if(!isProductExists(productId)){
            //TODO: calculate the new price of the product if needed (not for now)
            products.put(productId,new ProductWithQuantity(product,quantity));
        }
        else{
            throw new RuntimeException("Product is already exists, change the quantity of the product if needed");
        }


    }

    public void removeProduct(int productId) {
        if(products == null){
            throw new RuntimeException("Products has not been initialized");
        }

        if(!products.containsKey(productId)){
            throw new RuntimeException("Product with id: " + productId + " not found");
        }

        products.remove(productId);
    }

    public Boolean isProductExists(int productId) {
        return products.containsKey(productId);
    }

    public void changeProductQuantity(int productId, int quantity) {
      try{
          ProductWithQuantity pq = getProduct(productId);
          pq.setQuantity(quantity);

      }
      catch(Exception e){
          throw new RuntimeException(e.getMessage());
        }


    }

    public Map<Integer, ProductWithQuantity> getProducts() {
        return products;
    }


}
