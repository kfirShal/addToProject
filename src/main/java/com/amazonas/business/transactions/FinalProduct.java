package com.amazonas.business.transactions;

import com.amazonas.business.inventory.Product;

public record FinalProduct (
        String id,
        String name,
        double price,
        String category,
        String description
){
    public FinalProduct(Product product, String id){
        this(id, product.productName(), product.price(), product.category(), product.description());
    }
}
