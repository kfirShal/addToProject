package com.amazonas.backend.business.permissions;

import com.amazonas.backend.business.permissions.actions.MarketActions;
import com.amazonas.backend.business.permissions.actions.StoreActions;
import com.amazonas.backend.business.permissions.actions.UserActions;
import com.amazonas.backend.business.permissions.profiles.PermissionsProfile;
import com.amazonas.backend.business.permissions.profiles.RegisteredUserPermissionsProfile;
import com.amazonas.backend.repository.PermissionsProfileRepository;
import com.amazonas.common.utils.ReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@SuppressWarnings({"LoggingSimilarMessage", "BooleanMethodIsAlwaysInverted"})
@Component
public class PermissionsController {

    private static final Logger log = LoggerFactory.getLogger(PermissionsController.class);

    private final PermissionsProfile defaultProfile;
    private final PermissionsProfile guestProfile;
    private final ReadWriteLock lock;
    private final PermissionsProfileRepository repository;

    public PermissionsController(PermissionsProfile defaultRegisteredUserPermissionsProfile,
                                 PermissionsProfile guestPermissionsProfile,
                                 PermissionsProfileRepository permissionsProfileRepository) {
        defaultProfile = defaultRegisteredUserPermissionsProfile;
        guestProfile = guestPermissionsProfile;
        lock = new ReadWriteLock();
        this.repository = permissionsProfileRepository;
    }
    
    public boolean addPermission(String userId, UserActions action) {
        log.debug("Adding action {} to user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.addUserActionPermission(action);
        log.debug("action was {}", result? "added" : "not added");
        return result;
    }

    public boolean removePermission(String userId, UserActions action) {
        log.debug("Removing action {} from user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.removeUserActionPermission(action);
        log.debug("action was {}", result? "removed" : "not removed");
        return result;
    }

    public boolean addPermission(String userId, String storeId, StoreActions action) {
        log.debug("Adding action {} to user {} for store {}", action, userId, storeId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.addStorePermission(storeId, action);
        log.debug("action was {}", result? "added" : "not added");
        return result;
    }

    public boolean removePermission(String userId, String storeId, StoreActions action) {
        log.debug("Removing action {} from user {} for store {}", action, userId, storeId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.removeStorePermission(storeId, action);
        log.debug("action was {}", result? "removed" : "not removed");
        return result;
    }

    public boolean addPermission(String userId, MarketActions action) {
        log.debug("Adding action {} to user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.addMarketActionPermission(action);
        log.debug("action was {}", result? "added" : "not added");
        return result;
    }

    public boolean checkPermission(String userId, UserActions action) {
        log.debug("Checking action {} for user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.hasPermission(action);
        log.debug("action is {}", result? "granted" : "denied");
        return result;
    }

    public boolean checkPermission(String userId, String storeId, StoreActions action) {
        log.debug("Checking action {} for user {} for store {}", action, userId, storeId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.hasPermission(storeId, action);
        log.debug("action is {}", result? "granted" : "denied");
        return result;
    }

    public boolean checkPermission(String userId, MarketActions action) {
        log.debug("Checking action {} for user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.hasPermission(action);
        log.debug("action is {}", result? "granted" : "denied");
        return result;
    }

    public void registerUser(String userId) {
        log.debug("Registering user {}", userId);
        RegisteredUserPermissionsProfile newProfile = new RegisteredUserPermissionsProfile(userId, defaultProfile);
        registerUser(userId, newProfile, "User already registered");
    }

    public void registerGuest(String userId) {
        log.debug("Registering guest {}", userId);
        registerUser(userId,guestProfile, "Guest already registered");
    }

    public void removeUser(String userId) {
        log.debug("Removing user {}", userId);
        removeUser(userId, "User not registered");
        log.debug("User removed successfully");
    }

    public void removeGuest(String userId) {
        log.debug("Removing guest {}", userId);
        removeUser(userId, "Guest not registered");
        log.debug("Guest removed successfully");
    }

    private void registerUser(String userId, PermissionsProfile profile , String failMessage) {
        try{
            lock.acquireWrite();
            repository.addUser(userId, profile);
        } finally{
            lock.releaseWrite();
        }
    }

    private void removeUser(String userId, String failMessage) {
        try{
            lock.acquireWrite();
            var removed = repository.removeUser(userId);

            if(removed == null) {
                log.error(failMessage);
                throw new IllegalArgumentException(failMessage);
            }
        } finally {
            lock.releaseWrite();
        }
    }

    @NonNull
    private PermissionsProfile getPermissionsProfile(String userId) {
        log.trace("Fetching permissions profile for user {}", userId);
        lock.acquireRead();
        PermissionsProfile profile = repository.getPermissionsProfile(userId);
        lock.releaseRead();
        if(profile == null) {
            log.error("User not registered");
            throw new IllegalArgumentException("User not registered");
        }
        return profile;
    }
}
