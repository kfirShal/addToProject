package com.amazonas.business.transactions;

import com.amazonas.utils.ReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Component("transactionsController")
public class TransactionsController {
    private static final Logger log = LoggerFactory.getLogger(TransactionsController.class);

    //=================================================================
    //TODO: replace this with a database
    private final Map<String,List<Transaction>> userIdToTransactions;
    private final Map<String,List<Transaction>> storeIdToTransactions;
    //=================================================================

    ReadWriteLock lock = new ReadWriteLock();

    public TransactionsController() {
        storeIdToTransactions = new HashMap<>();
        userIdToTransactions = new HashMap<>();
    }

    public void documentTransaction(Transaction transaction){
        try {
            lock.acquireWrite();
            log.debug("Documenting transaction for user {} in store {}", transaction.userId(), transaction.storeId());
            userIdToTransactions.computeIfAbsent(transaction.userId(), _ -> new LinkedList<>()).add(transaction);
            storeIdToTransactions.computeIfAbsent(transaction.storeId(), _ -> new LinkedList<>()).add(transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    public List<Transaction> getTransactionByUser(String userId){
        try {
            lock.acquireRead();
            log.debug("Getting transactions for user {}", userId);
            return userIdToTransactions.getOrDefault(userId, new LinkedList<>());
        } finally {
            lock.releaseRead();
        }
    }

    public List<Transaction> getTransactionByStore(String storeId){
        try {
            lock.acquireRead();
            log.debug("Getting transactions for store {}", storeId);
            return storeIdToTransactions.getOrDefault(storeId, new LinkedList<>());
        } finally {
            lock.releaseRead();
        }
    }

}
