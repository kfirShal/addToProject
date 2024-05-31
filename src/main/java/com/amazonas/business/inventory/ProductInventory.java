package com.amazonas.business.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.Set;
import java.util.stream.Collectors;

public class ProductInventory {

    private static final Logger log = LoggerFactory.getLogger(ProductInventory.class);

    private final ConcurrentMap<String, Product> idToProduct;
    private final ConcurrentMap<String, Integer> idToQuantity;
    private final Set<String> disabledProductsId;

    public  ProductInventory(){
        idToProduct = new ConcurrentHashMap<>();
        idToQuantity = new ConcurrentHashMap<>();
        disabledProductsId = ConcurrentHashMap.newKeySet();
    }

    public boolean nameExists(String productName){
        return idToProduct.entrySet().stream()
                .anyMatch((x -> x.getValue().productName().equalsIgnoreCase(productName)));
    }
    public void addProduct(Product product) {
        product.setProductId(UUID.randomUUID().toString());
        log.debug("Adding product {} with id {} to inventory", product.productName(), product.productId());
        idToProduct.put(product.productId(),product);
    }

    public boolean updateProduct(Product product) {

        log.debug("Updating product {} with id {} in inventory", product.productName(), product.productId());

        // we want to make sure the object is the same object
        // so we can update it for the entire system
        if(idToProduct.containsKey(product.productId())) {
            Product product1 = idToProduct.get(product.productId());
            product1.setProductName(product.productName());
            product1.setCategory(product.category());
            product1.setRating(product.rating());
            product1.setPrice(product.price());
            product1.setDescription(product.description());
            return true;
        }
        return false;
    }

    public boolean removeProduct(String productId) {
        log.debug("Removing product with id {} from inventory", productId);

        if(!disabledProductsId.contains(productId)){
            return false;
        }
        idToProduct.remove(productId);
        idToQuantity.remove(productId);
        return true;
    }

    public void setQuantity(String productId, int quantity) {
        log.debug("Setting quantity of product with id {} to {} in inventory", productId, quantity);
        idToQuantity.put(productId, quantity);
    }


    /**
     * @return the quantity of the product. -1 if the product is not in the inventory
     */
    public int getQuantity(String productId) {
        log.debug("Getting quantity of product with id {} in inventory", productId);
        return idToQuantity.getOrDefault(productId, -1);
    }

    public boolean enableProduct(String productId) {
        if(disabledProductsId.contains(productId)){
            disabledProductsId.remove(productId);
            return true;
        }
        return false;
    }

    public boolean disableProduct(String productId) {
        if(!disabledProductsId.contains(productId)){
            disabledProductsId.add(productId);
            return true;
        }
        return false;
    }

    public boolean isProductDisabled(String productId) {
        return disabledProductsId.contains(productId);
    }

    public Set<Product> getAllAvailableProducts(){
        return idToProduct.values().stream()
                .filter(product -> !disabledProductsId.contains(product)
                                    && idToQuantity.get(product.productId()) > 0)
                .collect(Collectors.toSet());
    }
}
