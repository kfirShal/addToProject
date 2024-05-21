package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;

public class ProductEntry {
    private Product product;
    private double discount;
    public ProductEntry(Product product, double discount){
        this.product = product;
        this.discount=discount;
    }
}
