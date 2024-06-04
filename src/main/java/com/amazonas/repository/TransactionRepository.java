package com.amazonas.repository;

import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.userProfiles.User;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.TransactionMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import com.amazonas.utils.ReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TransactionRepository extends AbstractCachingRepository<Transaction> {


    private static final Logger log = LoggerFactory.getLogger(TransactionRepository.class);

    //=================================================================
    //TODO: replace this with a database query
    private final Map<String, List<Transaction>> userIdToTransactions;
    private final Map<String, List<Transaction>> storeIdToTransactions;
    //=================================================================

    ReadWriteLock lock = new ReadWriteLock();


    public TransactionRepository(TransactionMongoCollection repo) {
        super(repo);
        userIdToTransactions = new HashMap<>();
        storeIdToTransactions = new HashMap<>();
    }

    public Collection<Transaction> getWaitingShipment(String storeId) {
        return null;
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