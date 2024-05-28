package com.amazonas.acceptanceTests;

import com.amazonas.business.transactions.Transaction;

public class RealTransactionBridge implements TransactionBridge {
    @Override
    public boolean documentTransaction(Transaction transaction) {
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
