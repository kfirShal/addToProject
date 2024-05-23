package com.amazonas.acceptanceTests;

public class RealTransactionBridge implements TransactionBridge {
    @Override
    public boolean documentTransaction(String productName, String productDescription) {
        return false;
    }

    @Override
    public boolean getTransactionByUser(String userId) {
        return false;
    }

    @Override
    public boolean getTransactionByStore(String storeId) {
        return false;
    }
}
