package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.stores.purchasePolicy.PurchasePolicy;
import org.springframework.security.core.parameters.P;

import java.util.Iterator;
import java.util.List;

public class Store {
    public Rating storeRate;
    public List<Product> products;






    //functions

    public boolean enableProduct(Product toEnable){
        Iterator it = products.iterator();
        while ((it.hasNext())){
            Product curr = (Product) it.next();
            if(curr.productID() == toEnable.productID()){
                if(curr.)
            }
        }
    }

    public  boolean updateProduct(Product toUpdate){
        Iterator it = products.iterator();
        while(it.hasNext()){
            Product curr = (Product) it.next();
            if(curr.productID() == toUpdate.productID()){
                products.remove(curr);
                products.add(toUpdate);
                return true;
            }
        }
        return false;
    }
    public  boolean addProduct(Product toAdd){
        if(products.contains(toAdd)){
            return false;
        }
        products.add(toAdd);
        return true;
    }
    public  boolean removeProduct(Product toRemove){
        if(products.contains(toRemove)){
            products.remove(toRemove);
            return true;
        }
        return false;
    }
    public List<Product> searchProduct(SearchRequest request) {
        return null;
    }
}
