package com.amazonas.backend.repository;

import com.amazonas.backend.business.authentication.UserCredentials;
import com.amazonas.backend.repository.abstracts.AbstractCachingRepository;
import com.amazonas.backend.repository.mongoCollections.UserCredentialsMongoCollection;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component("userCredentialsRepository")
public class UserCredentialsRepository extends AbstractCachingRepository<UserCredentials> {

    //=================================================================
    //TODO: replace this with a database
    //Temporary storage for hashed passwords until we have a database
    private final Map<String, String> userIdToHashedPassword;
    //=================================================================

    private final ReadWriteLock permissionsProfileLock;

    public UserCredentialsRepository(UserCredentialsMongoCollection repo) {
        super(repo);
        userIdToHashedPassword = new HashMap<>();
        permissionsProfileLock = new ReadWriteLock();
    }

    public String getHashedPassword(String userId) {
        permissionsProfileLock.acquireRead();
        try {
            return userIdToHashedPassword.get(userId);
        } finally {
            permissionsProfileLock.releaseRead();
        }
    }

    public void saveHashedPassword(String userId, String encodedPassword) {
        permissionsProfileLock.acquireWrite();
        try {
            userIdToHashedPassword.put(userId, encodedPassword);
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }

    public void insert(UserCredentials newUser) {
        saveHashedPassword(newUser.userId(), newUser.password());
    }

    public void save(UserCredentials updatedUser) {
        saveHashedPassword(updatedUser.userId(), updatedUser.password());
    }

    public void deleteById(String username) {
        userIdToHashedPassword.remove(username);
    }

    public UserCredentials findById(String username) {
        return new UserCredentials(username, userIdToHashedPassword.get(username));
    }

    public boolean existsById(String username) {
        return userIdToHashedPassword.containsKey(username);
    }

    public void saveGuest(String userId, String hashedPassword) {
        //TODO: change this when we have a database. don't save to the db. just in memory.
        saveHashedPassword(userId, hashedPassword);
    }

    public void deleteGuest(String userId) {
        //TODO: change this when we have a database. don't save to the db. just in memory.
        deleteById(userId);
    }
}