package com.amazonas.business.inventory;

public record Product(
    String id,
    String name,
    String description,
    double price,
    int quantity
) {

}
