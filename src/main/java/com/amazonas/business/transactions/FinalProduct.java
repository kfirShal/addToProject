package com.amazonas.business.transactions;

import com.amazonas.business.inventory.Product;

public record FinalProduct (
        String id,
        String name,
        String description

){
    public FinalProduct(Product product, String id){
        this(id, product.name(), product.description());
    }
}
