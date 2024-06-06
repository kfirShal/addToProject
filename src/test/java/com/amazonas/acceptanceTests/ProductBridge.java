package com.amazonas.acceptanceTests;

public interface ProductBridge {
    boolean addProduct(String productName, String productDescription);
    boolean removeProduct(String productName);
    boolean updateProduct(String productName, String productDescription);

    void testAddProduct();

    void testAddProductValid();

    void testAddProductInvalid();

    void testAddProductDuplicate();

    void testRemoveProductValid();

    void testRemoveProductInvalid();

    void testRemoveProductNonexistent();

    void testUpdateProductValid();

    void testUpdateProductInvalid();

    void testUpdateProductNonexistent();

    void testSearchProductByName();

    void testSearchProductByCategory();

    void testSearchProductByPriceRange();
}
