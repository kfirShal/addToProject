package com.amazonas.backend.repository;

import com.amazonas.backend.business.userProfiles.User;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.mongoCollections.UserMongoCollection;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("userRepository")
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