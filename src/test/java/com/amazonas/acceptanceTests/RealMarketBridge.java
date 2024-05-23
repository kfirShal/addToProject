package com.amazonas.acceptanceTests;

public class RealMarketBridge implements MarketBridge{
    @Override
    public boolean searchProduct(String productName) {
        return false;
    }

    @Override
    public boolean makePurchase(String productName, String productDescription) {
        return false;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean shutdown() {
        return false;
    }

    @Override
    public boolean restart() {
        return false;
    }
}
