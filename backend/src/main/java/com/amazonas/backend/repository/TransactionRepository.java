package com.amazonas.backend.repository;

import com.amazonas.backend.business.transactions.Transaction;
import com.amazonas.backend.business.transactions.TransactionState;
import com.amazonas.common.utils.ReadWriteLock;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.mongoCollections.TransactionMongoCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("transactionRepository")
public class TransactionRepository extends AbstractCachingRepository<Transaction> {


    private static final Logger log = LoggerFactory.getLogger(TransactionRepository.class);

    //=================================================================
    //TODO: replace this with a database query
    private final Map<String, List<Transaction>> userIdToTransactions;
    private final Map<String, List<Transaction>> storeIdToTransactions;
    //=================================================================

    private final Map<String, Transaction> transactionIdToTransaction;
    private final ReadWriteLock transactionLock;

    ReadWriteLock lock = new ReadWriteLock();


    public TransactionRepository(TransactionMongoCollection repo) {
        super(repo);
        userIdToTransactions = new HashMap<>();
        storeIdToTransactions = new HashMap<>();
        transactionIdToTransaction = new HashMap<>();
        transactionLock = new ReadWriteLock();
    }

    public Collection<Transaction> getWaitingShipment(String storeId) {

        //TODO: replace this with a database query

        try {
            lock.acquireRead();
            log.debug("Getting waiting shipment transactions for store {}", storeId);
            return transactionIdToTransaction.values().stream()
                    .filter(t -> t.state() == TransactionState.PENDING_SHIPMENT).toList();
        } finally {
            lock.releaseRead();
        }
    }

    public void addNewTransaction(Transaction transaction){
        try {
            lock.acquireWrite();
            log.debug("Documenting transaction for user {} in store {}", transaction.userId(), transaction.storeId());
            userIdToTransactions.computeIfAbsent(transaction.userId(), _ -> new LinkedList<>()).add(transaction);
            storeIdToTransactions.computeIfAbsent(transaction.storeId(), _ -> new LinkedList<>()).add(transaction);
            transactionIdToTransaction.put(transaction.transactionId(), transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    public List<Transaction> getTransactionHistoryByUser(String userId){
        try {
            lock.acquireRead();
            log.debug("Getting transactions for user {}", userId);
            return userIdToTransactions.getOrDefault(userId, new LinkedList<>());
        } finally {
            lock.releaseRead();
        }
    }

    public List<Transaction> getTransactionHistoryByStore(String storeId){
        try {
            lock.acquireRead();
            log.debug("Getting transactions for store {}", storeId);
            return storeIdToTransactions.getOrDefault(storeId, new LinkedList<>());
        } finally {
            lock.releaseRead();
        }
    }

    public Transaction getTransactionById(String transactionId) {
        transactionLock.acquireRead();
        try {
            return transactionIdToTransaction.get(transactionId);
        } finally {
            transactionLock.releaseRead();
        }
    }

    public void updateTransaction(Transaction transaction) {
        transactionLock.acquireWrite();
        try {
            transactionIdToTransaction.put(transaction.transactionId(), transaction);
        } finally {
            transactionLock.releaseWrite();
        }
    }

    public void saveAllTransactions(Collection<Transaction> transactions) {
        transactionLock.acquireWrite();
        try {
            transactions.forEach(transaction -> this.transactionIdToTransaction.put(transaction.transactionId(), transaction));
        } finally {
            transactionLock.releaseWrite();
        }
    }

}