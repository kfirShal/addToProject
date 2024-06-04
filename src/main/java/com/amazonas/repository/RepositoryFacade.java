package com.amazonas.repository;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.permissions.profiles.PermissionsProfile;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.User;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.*;

@Component("repositoryFacade")
public class RepositoryFacade {

    private final Map<String, User> userCache;
    private final Map<String, Product> productCache;
    private final Map<String, Store> storeCache;
    private final Map<String, PermissionsProfile> permissionsProfileCache;
    private final Map<String, Transaction> transactionCache;
    private final Map<String, List<Reservation>> reservationCache;
    private final ReadWriteLock userLock;
    private final ReadWriteLock productLock;
    private final ReadWriteLock storeLock;
    private final ReadWriteLock permissionsProfileLock;
    private final ReadWriteLock transactionLock;
    private final ReadWriteLock reservationLock;

    public RepositoryFacade() {
        userCache = new HashMap<>();
        productCache = new HashMap<>();
        storeCache = new HashMap<>();
        permissionsProfileCache = new HashMap<>();
        transactionCache = new HashMap<>();
        reservationCache = new HashMap<>();
        userLock = new ReadWriteLock();
        productLock = new ReadWriteLock();
        storeLock = new ReadWriteLock();
        permissionsProfileLock = new ReadWriteLock();
        transactionLock = new ReadWriteLock();
        reservationLock = new ReadWriteLock();
    }

    public User getUser(String userId) {
        userLock.acquireRead();
        try {
            return userCache.get(userId);
        } finally {
            userLock.releaseRead();
        }
    }

    public Product getProduct(String productId) {
        productLock.acquireRead();
        try {
            return productCache.get(productId);
        } finally {
            productLock.releaseRead();
        }
    }

    public Store getStore(String storeId) {
        storeLock.acquireRead();
        try {
            return storeCache.get(storeId);
        } finally {
            storeLock.releaseRead();
        }
    }

    public PermissionsProfile getPermissionsProfile(String profileId) {
        permissionsProfileLock.acquireRead();
        try {
            return permissionsProfileCache.get(profileId);
        } finally {
            permissionsProfileLock.releaseRead();
        }
    }

    public Transaction getTransaction(String transactionId) {
        transactionLock.acquireRead();
        try {
            return transactionCache.get(transactionId);
        } finally {
            transactionLock.releaseRead();
        }
    }

    public void saveUser(User user) {
        userLock.acquireWrite();
        try {
            userCache.put(user.getUserId(), user);
        } finally {
            userLock.releaseWrite();
        }
    }
    public Collection<Store> getAllStores(){
        return storeCache.values();
    }
    public void saveAllUsers(Collection<User> users) {
        userLock.acquireWrite();
        try {
            users.forEach(user -> userCache.put(user.getUserId(), user));
        } finally {
            userLock.releaseWrite();
        }
    }

    public void saveProduct(Product product) {
        productLock.acquireWrite();
        try {
            productCache.put(product.productId(), product);
        } finally {
            productLock.releaseWrite();
        }
    }

    public void saveAllProducts(Collection<Product> products) {
        productLock.acquireWrite();
        try {
            products.forEach(product -> productCache.put(product.productId(), product));
        } finally {
            productLock.releaseWrite();
        }
    }

    public void saveStore(Store store) {
        storeLock.acquireWrite();
        try {
            storeCache.put(store.getStoreId(), store);
        } finally {
            storeLock.releaseWrite();
        }
    }

    public void saveAllStores(Collection<Store> stores) {
        storeLock.acquireWrite();
        try {
            stores.forEach(store -> storeCache.put(store.getStoreId(), store));
        } finally {
            storeLock.releaseWrite();
        }
    }

    public void savePermissionsProfile(PermissionsProfile profile) {
        permissionsProfileLock.acquireWrite();
        try {
            permissionsProfileCache.put(profile.getUserId(), profile);
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }

    public void saveAllPermissionsProfiles(Collection<PermissionsProfile> profiles) {
        permissionsProfileLock.acquireWrite();
        try {
            profiles.forEach(profile -> permissionsProfileCache.put(profile.getUserId(), profile));
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }

    public void saveTransaction(Transaction transaction) {
        transactionLock.acquireWrite();
        try {
            transactionCache.put(transaction.transactionId(), transaction);
        } finally {
            transactionLock.releaseWrite();
        }
    }

    public void saveAllTransactions(Collection<Transaction> transactions) {
        transactionLock.acquireWrite();
        try {
            transactions.forEach(transaction -> transactionCache.put(transaction.transactionId(), transaction));
        } finally {
            transactionLock.releaseWrite();
        }
    }

    public void saveReservation(String userId, Reservation reservation) {
        reservationLock.acquireWrite();
        try {
            reservationCache.computeIfAbsent(userId, _ -> new LinkedList<>()).add(reservation);
        } finally {
            reservationLock.releaseWrite();
        }
    }

    public List<Reservation> getReservations(String userId){
        reservationLock.acquireRead();
        try {
            return reservationCache.getOrDefault(userId, Collections.emptyList());
        } finally {
            reservationLock.releaseRead();
        }
    }

    public void removeReservation(String userId, Reservation reservation) {
        reservationLock.acquireWrite();
        try {
            reservationCache.getOrDefault(userId, Collections.emptyList()).remove(reservation);
        } finally {
            reservationLock.releaseWrite();
        }
    }
}