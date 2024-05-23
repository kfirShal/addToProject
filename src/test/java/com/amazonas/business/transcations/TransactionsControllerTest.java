package com.amazonas.business.transcations;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.CreditCard;
import com.amazonas.business.payment.Paypal;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.transactions.TransactionsController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TransactionsControllerTest {

    private TransactionsController transactionsController;
    private CreditCard creditCard;
    private Paypal paypal;
    private Product iPhone14ProMax;
    private Map<Product, Double> map;

    @BeforeEach
    void setUp() {
        transactionsController = new TransactionsController();
        creditCard = new CreditCard();
        paypal = new Paypal();
        iPhone14ProMax = new Product("0", "iPhone14ProMax", 100.0, "Technologies", "phone", 10);
        map = new HashMap<>();
        map.put(iPhone14ProMax, 100.0);
    }

    @Test
    void documentTransaction_shouldAddTransactionToUserAndStore() {
        Transaction transaction = new Transaction("store1", "user1", creditCard, LocalDateTime.now(), map);
        transactionsController.documentTransaction(transaction);

        List<Transaction> userTransactions = transactionsController.getTransactionByUser("user1");
        List<Transaction> storeTransactions = transactionsController.getTransactionByStore("store1");

        assertNotNull(userTransactions);
        assertNotNull(storeTransactions);
        assertEquals(1, userTransactions.size());
        assertEquals(1, storeTransactions.size());
        assertEquals(transaction, userTransactions.get(0));
        assertEquals(transaction, storeTransactions.get(0));
    }

    @Test
    void documentTransaction_shouldNotAddInvalidTransaction() {
        List<Transaction> initialStoreTransactions = transactionsController.getTransactionByStore("store1");
        assertTrue(initialStoreTransactions.isEmpty(), "Initial store transactions list should be empty");

        assertThrows(IllegalArgumentException.class, () -> {
            Transaction invalidTransaction = new Transaction("store1", null, paypal, LocalDateTime.now(), map);
            transactionsController.documentTransaction(invalidTransaction);
        });

        List<Transaction> storeTransactions = transactionsController.getTransactionByStore("store1");
        assertEquals(initialStoreTransactions.size(), storeTransactions.size(), "Store transactions list should remain unchanged");
    }

    @Test
    void getTransactionByUser_shouldReturnTransactionsForKnownUser() {
        Transaction transaction = new Transaction("store1", "user1", creditCard, LocalDateTime.now(), map);
        transactionsController.documentTransaction(transaction);

        List<Transaction> userTransactions = transactionsController.getTransactionByUser("user1");

        assertNotNull(userTransactions);
        assertEquals(1, userTransactions.size());
        assertEquals(transaction, userTransactions.get(0));
    }

    @Test
    void getTransactionByUser_shouldReturnEmptyListForUnknownUser() {
        List<Transaction> userTransactions = transactionsController.getTransactionByUser("unknownUser");
        assertNotNull(userTransactions);
        assertTrue(userTransactions.isEmpty());
    }

    @Test
    void getTransactionByStore_shouldReturnTransactionsForKnownStore() {
        Transaction transaction = new Transaction("store1", "user1", paypal, LocalDateTime.now(), map);
        transactionsController.documentTransaction(transaction);

        List<Transaction> storeTransactions = transactionsController.getTransactionByStore("store1");

        assertNotNull(storeTransactions);
        assertEquals(1, storeTransactions.size());
        assertEquals(transaction, storeTransactions.get(0));
    }

    @Test
    void getTransactionByStore_shouldReturnEmptyListForUnknownStore() {
        List<Transaction> storeTransactions = transactionsController.getTransactionByStore("unknownStore");
        assertNotNull(storeTransactions);
        assertTrue(storeTransactions.isEmpty());
    }
}
