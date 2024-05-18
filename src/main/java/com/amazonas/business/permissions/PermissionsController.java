package com.amazonas.business.permissions;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.permissions.profiles.PermissionsProfile;
import com.amazonas.business.permissions.profiles.RegisteredUserPermissionsProfile;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.userProfiles.UserActions;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

@Component
public class PermissionsController {

    private final PermissionsProfile defaultProfile;
    private final PermissionsProfile guestProfile;
    private final Map<String, PermissionsProfile> userIdToPermissionsProfile;
    private final Semaphore lock;

    public PermissionsController(PermissionsProfile defaultRegisteredUserPermissionsProfile,
                                 PermissionsProfile guestPermissionsProfile) {
        defaultProfile = defaultRegisteredUserPermissionsProfile;
        guestProfile = guestPermissionsProfile;
        userIdToPermissionsProfile = new HashMap<>();
        lock = new Semaphore(1,true);
    }
    
    public boolean addPermission(String userId, UserActions action) {
        PermissionsProfile profile = getPermissionsProfile(userId);
        return profile.addUserActionPermission(action);
    }

    public boolean removePermission(String userId, UserActions action) {
        PermissionsProfile profile = getPermissionsProfile(userId);
        return profile.removeUserActionPermission(action);
    }

    public boolean addPermission(String userId, String storeId, StoreActions action) {
        PermissionsProfile profile = getPermissionsProfile(userId);
        return profile.addStorePermission(storeId, action);
    }

    public boolean removePermission(String userId, String storeId, StoreActions action) {
        PermissionsProfile profile = getPermissionsProfile(userId);
        return profile.removeStorePermission(storeId, action);
    }

    public boolean addPermission(String userId, MarketActions action) {
        PermissionsProfile profile = getPermissionsProfile(userId);
        return profile.addMarketActionPermission(action);
    }

    public boolean checkPermission(String userId, UserActions action) {
        PermissionsProfile profile = getPermissionsProfile(userId);
        return profile.hasPermission(action);
    }

    public boolean checkPermission(String userId, String storeId, StoreActions action) {
        PermissionsProfile profile = getPermissionsProfile(userId);
        return profile.hasPermission(storeId, action);
    }

    public boolean checkPermission(String userId, MarketActions action) {
        PermissionsProfile profile = getPermissionsProfile(userId);
        return profile.hasPermission(action);
    }

    public void registerUser(String userId) {
        RegisteredUserPermissionsProfile newProfile = new RegisteredUserPermissionsProfile(userId, defaultProfile);
        registerUser(userId, newProfile, "User already registered");
    }

    public void registerGuest(String userId) {
        registerUser(userId,guestProfile, "Guest already registered");
    }

    public void removeUser(String userId) {
        removeUser(userId, "User not registered");
    }

    public void removeGuest(String userId) {
        removeUser(userId, "Guest not registered");
    }

    private void registerUser(String userId, PermissionsProfile profile , String failMessage) {
        lockAcquire();
        if(userIdToPermissionsProfile.containsKey(userId)) {
            lock.release();
            throw new IllegalArgumentException(failMessage);
        }
        userIdToPermissionsProfile.put(userId, profile);
        lock.release();
    }

    private void removeUser(String userId, String failMessage) {
        lockAcquire();
        var removed = userIdToPermissionsProfile.remove(userId);
        lock.release();

        if(removed == null) {
            throw new IllegalArgumentException(failMessage);
        }
    }

    @NonNull
    private PermissionsProfile getPermissionsProfile(String userId) {
        lockAcquire();
        PermissionsProfile profile = userIdToPermissionsProfile.get(userId);
        lock.release();
        if(profile == null) {
            throw new IllegalArgumentException("User not registered");
        }
        return profile;
    }

    private void lockAcquire() {
        try {
            lock.acquire();
        } catch (InterruptedException ignored) {}
    }
}
