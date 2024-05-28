package com.amazonas.acceptanceTests;

public interface ProductBridge {
    boolean addProduct(String productName, String productDescription);
    boolean removeProduct(String productName);
    boolean updateProduct(String productName, String productDescription);

    void testAddProduct();

    void testAddProductValid();
}
