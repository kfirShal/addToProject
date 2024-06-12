package com.amazonas.repository;

import com.amazonas.business.permissions.profiles.PermissionsProfile;
import com.amazonas.repository.abstracts.AbstractCachingRepository;
import com.amazonas.repository.mongoCollections.PermissionProfileMongoCollection;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component("permissionsProfileRepository")
public class PermissionsProfileRepository extends AbstractCachingRepository<PermissionsProfile> {

    private final Map<String, PermissionsProfile> userIdToPermissionsProfile;

    private final ReadWriteLock permissionsProfileLock;

    public PermissionsProfileRepository(PermissionProfileMongoCollection repo) {
        super(repo);
        userIdToPermissionsProfile = new HashMap<>();
        permissionsProfileLock = new ReadWriteLock();
    }

    public PermissionsProfile getPermissionsProfile(String profileId) {
        permissionsProfileLock.acquireRead();
        try {
            return userIdToPermissionsProfile.get(profileId);
        } finally {
            permissionsProfileLock.releaseRead();
        }
    }

    public void savePermissionsProfile(PermissionsProfile profile) {
        permissionsProfileLock.acquireWrite();
        try {
            userIdToPermissionsProfile.put(profile.getUserId(), profile);
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }

    public void saveAllPermissionsProfiles(Collection<PermissionsProfile> profiles) {
        permissionsProfileLock.acquireWrite();
        try {
            profiles.forEach(profile -> userIdToPermissionsProfile.put(profile.getUserId(), profile));
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }

    public void addUser(String userId, PermissionsProfile profile) {
        permissionsProfileLock.acquireWrite();
        try {
            userIdToPermissionsProfile.put(userId, profile);
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }

    public Object removeUser(String userId) {
        permissionsProfileLock.acquireWrite();
        try {
            return userIdToPermissionsProfile.remove(userId);
        } finally {
            permissionsProfileLock.releaseWrite();
        }
    }
}