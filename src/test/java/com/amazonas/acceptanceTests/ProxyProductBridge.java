package com.amazonas.acceptanceTests;

public class ProxyProductBridge implements ProductBridge {
    @Override
    public boolean addProduct(String productName, String productDescription) {
        return false;
    }

    @Override
    public boolean removeProduct(String productName) {
        return false;
    }

    @Override
    public boolean updateProduct(String productName, String productDescription) {
        return false;
    }

    @Override
    public void testAddProduct() {

    }
}
