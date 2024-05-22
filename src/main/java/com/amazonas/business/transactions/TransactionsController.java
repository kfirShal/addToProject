package com.amazonas.business.transactions;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

@Component("transactionsController")

public class TransactionsController {
    //=================================================================
    //TODO: replace this with a database
    //Temporary storage for hashed passwords until we have a database
    private final Map<String,List<Transaction>> userIdToTransactions;
    private final Map<String,List<Transaction>> storeIdToTransactions;

    //=================================================================
    private final Semaphore lock;

    public TransactionsController() {
        lock = new Semaphore(1,true);
        storeIdToTransactions = new HashMap<>();
        userIdToTransactions = new HashMap<>();
    }

    private void lockAcquire(){
        try {
            lock.acquire();
        } catch (InterruptedException ignored) {}
    }
    public void documentTransaction(Transaction transaction){
        lockAcquire();
        userIdToTransactions.get(transaction.userId()).add(transaction);
        storeIdToTransactions.get(transaction.storeId()).add(transaction);
        lock.release();
    }

    public List<Transaction> getTransactionByUser(String userId){
        lockAcquire();
        List<Transaction> result = userIdToTransactions.get(userId);
        lock.release();
        return result;
    }

    public List<Transaction> getTransactionByStore(String storeId){
        lockAcquire();
        List<Transaction> result = storeIdToTransactions.get(storeId);
        lock.release();
        return result;
    }

}
