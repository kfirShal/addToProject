package com.amazonas.acceptanceTests;

import com.amazonas.business.transactions.Transaction;

public interface TransactionBridge {
    boolean documentTransaction(Transaction transaction);
    boolean getTransactionByUser(String userId);
    boolean getTransactionByStore(String storeId);

}
