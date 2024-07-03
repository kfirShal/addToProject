package com.amazonas.common.permissions.profiles;

import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.permissions.actions.UserActions;
import com.amazonas.common.utils.ReadWriteLock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class UserPermissionsProfile implements PermissionsProfile {

    private final String userId;
    private final PermissionsProfile defaultProfile;
    private final Map<String,Set<StoreActions>> storeIdToAllowedStoreActions;
    private final Set<UserActions> allowedUserActions;
    private final Set<MarketActions> allowedMarketActions;
    private final ReadWriteLock lock;


    public UserPermissionsProfile(String userId, PermissionsProfile defaultProfile) {
        this.defaultProfile = defaultProfile;
        this.userId = userId;
        storeIdToAllowedStoreActions = new HashMap<>();
        allowedUserActions = new HashSet<>();
        allowedMarketActions = new HashSet<>();
        lock = new ReadWriteLock();
    }

    @Override
    public boolean addStorePermission(String storeId, StoreActions action) {
        lock.acquireWrite();
        Set<StoreActions> allowedActions = storeIdToAllowedStoreActions.computeIfAbsent(storeId, _ -> new HashSet<>());
        boolean output = allowedActions.add(action);
        lock.releaseWrite();
        return output;
    }

    @Override
    public boolean removeStorePermission(String storeId, StoreActions action) {
        boolean result;
        lock.acquireWrite();
        Set<StoreActions> allowedActions = storeIdToAllowedStoreActions.get(storeId);
        if (allowedActions != null) {
            result = allowedActions.remove(action);
            if (allowedActions.isEmpty()) {
                storeIdToAllowedStoreActions.remove(storeId);
            }
        } else {
            result = false;
        }
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean addUserActionPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        lock.acquireWrite();
        boolean result = allowedUserActions.add(action);
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean removeUserActionPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        lock.acquireWrite();
        boolean result = allowedUserActions.remove(action);
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean addMarketActionPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        lock.acquireWrite();
        boolean result = allowedMarketActions.add(action);
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean removeMarketActionPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        lock.acquireWrite();
        boolean result = allowedMarketActions.remove(action);
        lock.releaseWrite();
        return result;
    }

    @Override
    public boolean hasPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return true;
        }
        lock.acquireRead();
        boolean result = allowedUserActions.contains(action);
        lock.releaseRead();
        return result;
    }

    @Override
    public boolean hasPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return true;
        }
        lock.acquireRead();
        boolean result = allowedMarketActions.contains(action);
        lock.releaseRead();
        return result;

    }

    @Override
    public boolean hasPermission(String storeId, StoreActions action) {
        lock.acquireRead();
        Set<StoreActions> allowedActions = storeIdToAllowedStoreActions.get(storeId);
        boolean result = allowedActions != null && allowedActions.contains(action);
        lock.releaseRead();
        return result;
    }

    @Override
    public String getUserId() {
        return userId;
    }
}
