package com.amazonas.repository;

import com.amazonas.business.authentication.UserCredentials;
import com.amazonas.business.permissions.profiles.PermissionsProfile;
import com.amazonas.business.userProfiles.User;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.UserCredentialsMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserCredentialsRepository extends AbstractCachingRepository<UserCredentials> {

    private final Map<String, PermissionsProfile> permissionsProfileCache;
    private final ReadWriteLock permissionsProfileLock;

    public UserCredentialsRepository(UserCredentialsMongoCollection repo) {
        super(repo);
        permissionsProfileCache = new HashMap<>();
        permissionsProfileLock = new ReadWriteLock();
    }

    public PermissionsProfile getPermissionsProfile(String profileId) {
        permissionsProfileLock.acquireRead();
        try {
            return permissionsProfileCache.get(profileId);
        } finally {
            permissionsProfileLock.releaseRead();
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

}