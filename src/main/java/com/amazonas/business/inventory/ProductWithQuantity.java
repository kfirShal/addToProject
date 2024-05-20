package com.amazonas.business.inventory;

public record ProductWithQuantity(Product product, int quantity) {

    public void setQuantity(int quantity) {
    }
}
