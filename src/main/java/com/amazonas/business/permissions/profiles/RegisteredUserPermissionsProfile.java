package com.amazonas.business.permissions.profiles;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.userProfiles.UserActions;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RegisteredUserPermissionsProfile implements PermissionsProfile {

    private final String userId;
    private final PermissionsProfile defaultProfile;
    private final ConcurrentMap<String,Set<StoreActions>> storeIdToAllowedStoreActions;
    private final Set<UserActions> allowedUserActions;
    private final Set<MarketActions> allowedMarketActions;

    public RegisteredUserPermissionsProfile(String userId, PermissionsProfile defaultProfile) {
        this.defaultProfile = defaultProfile;
        this.userId = userId;
        storeIdToAllowedStoreActions = new ConcurrentHashMap<>();
        allowedUserActions = ConcurrentHashMap.newKeySet();
        allowedMarketActions = ConcurrentHashMap.newKeySet();
    }

    @Override
    public boolean addStorePermission(String storeId, StoreActions action) {
        Set<StoreActions> allowedActions = storeIdToAllowedStoreActions.computeIfAbsent(storeId, _ -> new HashSet<>());
        return allowedActions.add(action);
    }

    @Override
    public boolean removeStorePermission(String storeId, StoreActions action) {
        Set<StoreActions> allowedActions = storeIdToAllowedStoreActions.get(storeId);
        if (allowedActions == null) {
            return false;
        }
        boolean result = allowedActions.remove(action);
        if (allowedActions.isEmpty()) {
            storeIdToAllowedStoreActions.remove(storeId);
        }
        return result;
    }

    @Override
    public boolean addUserActionPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        return allowedUserActions.add(action);
    }

    @Override
    public boolean removeUserActionPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        return allowedUserActions.remove(action);
    }

    @Override
    public boolean addMarketActionPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        return allowedMarketActions.add(action);
    }

    @Override
    public boolean removeMarketActionPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        return allowedMarketActions.remove(action);
    }

    @Override
    public boolean hasPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return true;
        }
        return allowedUserActions.contains(action);
    }

    @Override
    public boolean hasPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return true;
        }
        return allowedMarketActions.contains(action);

    }

    @Override
    public boolean hasPermission(String storeId, StoreActions action) {
        Set<StoreActions> allowedActions = storeIdToAllowedStoreActions.get(storeId);
        return allowedActions != null && allowedActions.contains(action);
    }

    @Override
    public String getUserId() {
        return userId;
    }
}
