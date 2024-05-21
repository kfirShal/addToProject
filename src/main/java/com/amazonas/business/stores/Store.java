package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.stores.purchasePolicy.PurchasePolicy;
import com.amazonas.business.transactions.Transaction;
import org.springframework.security.core.parameters.P;

import java.security.Permission;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class Store {
    public int storeID;
    public Rating storeRate;
    private Boolean isOpen;
    public List<Product> products;

    private String info;


    //functions


    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

    public boolean isOpen(){
        return isOpen;
    }
    public boolean closeStore(){
        if(isOpen) {
            isOpen = false;
            return true;
        }
        return false;
    }
    public boolean openStore(){
        if(!isOpen) {
            isOpen = true;
            return true;
        }
        return false;
    }
    public boolean disableProduct(Product toEnable){
        for (Product curr : products) {
            if (Objects.equals(curr.productID(), toEnable.productID())) {
                if (Objects.equals(curr.productID(), toEnable.productID())) {
                    if (curr.getEnabled()) {
                        curr.setDisable();
                        return true;
                    }
                    return false;
                }
            }
        }
        return false;
    }
    public boolean enableProduct(Product toEnable){
        for (Product curr : products) {
            if (Objects.equals(curr.productID(), toEnable.productID())) {
                if (Objects.equals(curr.productID(), toEnable.productID())) {
                    if (curr.getEnabled())
                        return false;
                    curr.setEnabled();
                    return true;
                }
            }
        }
        return false;
    }

    public  boolean updateProduct(Product toUpdate){
        for (Product curr : products) {
            if (Objects.equals(curr.productID(), toUpdate.productID())) {
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
