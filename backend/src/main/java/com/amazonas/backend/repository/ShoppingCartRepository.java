package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.mongoCollections.ShoppingCartMongoCollection;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("shoppingCartRepository")
public class ShoppingCartRepository extends AbstractCachingRepository<ShoppingCart> {

    private final Map<String, ShoppingCart> cartCache; //TODO: REMOVE THIS
    private final ReadWriteLock userLock;

    public ShoppingCartRepository(ShoppingCartMongoCollection repo) {
        super(repo);
        userLock = new ReadWriteLock();
        cartCache = new HashMap<>();
    }

    public ShoppingCart getCart(String userId) {
        userLock.acquireRead();
        try {
            return cartCache.get(userId);
        } finally {
            userLock.releaseRead();
        }
    }

    public void saveCart(ShoppingCart cart) {
        userLock.acquireWrite();
        try {
            cartCache.put(cart.userId(), cart);
        } finally {
            userLock.releaseWrite();
        }
    }
}