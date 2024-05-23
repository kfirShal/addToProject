package com.amazonas.acceptanceTests;

public interface TransactionBridge {
    boolean documentTransaction(String productName, String productDescription);
    boolean getTransactionByUser(String userId);
    boolean getTransactionByStore(String storeId);

}
