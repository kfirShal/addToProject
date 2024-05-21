package com.amazonas.business.transactions;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

@Component("transactionsController")
public class TransactionsController {
    //=================================================================
    //TODO: replace this with a database
    private final ConcurrentMap<String,List<Transaction>> userIdToTransactions;
    private final ConcurrentMap<String,List<Transaction>> storeIdToTransactions;
    //=================================================================

    public TransactionsController() {
        storeIdToTransactions = new ConcurrentHashMap<>();
        userIdToTransactions = new ConcurrentHashMap<>();
    }

    public void documentTransaction(Transaction transaction){
        userIdToTransactions.computeIfAbsent(transaction.userId(), _ -> new ArrayList<>()).add(transaction);
        storeIdToTransactions.computeIfAbsent(transaction.storeId(), _ -> new ArrayList<>()).add(transaction);
    }

    public List<Transaction> getTransactionByUser(String userId){
        return userIdToTransactions.get(userId);
    }

    public List<Transaction> getTransactionByStore(String storeId){
        return storeIdToTransactions.get(storeId);
    }

}
