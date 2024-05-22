package com.amazonas.business.inventory;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("ProductInventory")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class ProductInventory {

    private final GlobalProductTracker tracker;
    private final ConcurrentMap<String, Product> idToProduct;
    private final ConcurrentMap<String, Integer> idToQuantity;
    private final Set<Product> disabledProducts;

    public  ProductInventory(GlobalProductTracker tracker){
        this.tracker = tracker;
        idToProduct = new ConcurrentHashMap<>();
        idToQuantity = new ConcurrentHashMap<>();
        disabledProducts = ConcurrentHashMap.newKeySet();
    }

    public void addProduct(Product product) {
        String newId;
        do{
            newId = UUID.randomUUID().toString();
            product.setProductID(newId);
        }while (tracker.productExists(product));
        idToProduct.put(product.productId(),product);
        idToQuantity.put(product.productId(),0);
    }

    public boolean removeProduct(Product product) {
        if(!disabledProducts.contains(product)){
            return false;
        }
        idToProduct.remove(product.productId());
        idToQuantity.remove(product.productId());
        return true;
    }

    public boolean updateProduct(Product product){
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

    public void setQuantity(Product product, int quantity) {
        idToQuantity.put(product.productId(), quantity);
    }

    public int getQuantity(Product product){
        return idToQuantity.getOrDefault(product.productId(), -1);
    }

    public boolean enableProduct(Product toEnable) {
        if(disabledProducts.contains(toEnable)){
            disabledProducts.remove(toEnable);
            return true;
        }
        return false;
    }

    public boolean disableProduct(Product toDisable) {
        if(!disabledProducts.contains(toDisable)){
            disabledProducts.add(toDisable);
            return true;
        }
        return false;
    }

    public boolean isProductDisabled(Product product) {
        return disabledProducts.contains(product);
    }
}
