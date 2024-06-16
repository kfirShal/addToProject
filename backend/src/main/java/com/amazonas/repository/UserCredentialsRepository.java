package com.amazonas.repository;

import com.amazonas.business.authentication.UserCredentials;
import com.amazonas.common.utils.ReadWriteLock;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.UserCredentialsMongoCollection;
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
}