package com.amazonas.repository;

import com.amazonas.business.stores.Store;
import com.amazonas.business.userProfiles.User;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.StoreMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserRepository extends AbstractCachingRepository<User> {

    private final Map<String, User> userCache;
    private final ReadWriteLock userLock;

    public UserRepository(UserMongoCollection repo) {
        super(repo);
        userLock = new ReadWriteLock();
        userCache = new HashMap<>();
    }

    public User getUser(String userId) {
        userLock.acquireRead();
        try {
            return userCache.get(userId);
        } finally {
            userLock.releaseRead();
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

    public void saveAllUsers(Collection<User> users) {
        userLock.acquireWrite();
        try {
            users.forEach(user -> userCache.put(user.getUserId(), user));
        } finally {
            userLock.releaseWrite();
        }
    }

    public boolean userIdExists(String userId) {
        userLock.acquireRead();
        try {
            return userCache.containsKey(userId);
        } finally {
            userLock.releaseRead();
        }
    }

}