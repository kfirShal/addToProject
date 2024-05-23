package com.amazonas.acceptanceTests;

public interface MarketBridge {
    boolean searchProduct(String productName);
    boolean makePurchase(String productName, String productDescription);
    boolean start();
    boolean shutdown();
    boolean restart();

}
