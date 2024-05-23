package com.amazonas.business.transactions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("transactionsController")
public class TransactionsController {
    private static final Logger log = LoggerFactory.getLogger(TransactionsController.class);

    //=================================================================
    //TODO: replace this with a database
    private final ConcurrentMap<String,List<Transaction>> userIdToTransactions;
    private final ConcurrentMap<String,List<Transaction>> storeIdToTransactions;
    //=================================================================

    public TransactionsController() {
        userIdToTransactions = new ConcurrentHashMap<>();
        storeIdToTransactions = new ConcurrentHashMap<>();
    }

    public void documentTransaction(Transaction transaction){
        if (transaction.userId() == null || transaction.storeId() == null) {
            throw new IllegalArgumentException("User ID and Store ID cannot be null");
        }
        log.debug("Documenting transaction for user {} in store {}", transaction.userId(), transaction.storeId());
        userIdToTransactions.computeIfAbsent(transaction.userId(), _ -> new ArrayList<>()).add(transaction);
        storeIdToTransactions.computeIfAbsent(transaction.storeId(), _ -> new ArrayList<>()).add(transaction);
    }

    public List<Transaction> getTransactionByUser(String userId){
        log.debug("Getting transactions for user {}", userId);
        return userIdToTransactions.getOrDefault(userId, new LinkedList<>());
    }

    public List<Transaction> getTransactionByStore(String storeId){
        log.debug("Getting transactions for store {}", storeId);
        return storeIdToTransactions.getOrDefault(storeId, new LinkedList<>());
    }

}