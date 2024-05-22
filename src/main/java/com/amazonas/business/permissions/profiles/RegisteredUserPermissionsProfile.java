package com.amazonas.business.permissions.profiles;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.userProfiles.UserActions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RegisteredUserPermissionsProfile implements PermissionsProfile {

    private final String userId;
    private final PermissionsProfile defaultProfile;
    private final Map<String,Set<StoreActions>> storeIdToAllowedStoreActions;
    private final Set<UserActions> allowedUserActions;
    private final Set<MarketActions> allowedMarketActions;

    public RegisteredUserPermissionsProfile(String userId, PermissionsProfile defaultProfile) {
        this.defaultProfile = defaultProfile;
        this.userId = userId;
        storeIdToAllowedStoreActions = new HashMap<>();
        allowedUserActions = new HashSet<>();
        allowedMarketActions = new HashSet<>();
    }

    @Override
    public boolean addStorePermission(String storeId, StoreActions action) {
        synchronized (storeIdToAllowedStoreActions){
            Set<StoreActions> allowedActions = storeIdToAllowedStoreActions.computeIfAbsent(storeId, x -> new HashSet<>());
            return allowedActions.add(action);
        }
    }

    @Override
    public boolean removeStorePermission(String storeId, StoreActions action) {
        synchronized (storeIdToAllowedStoreActions){
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
    }

    @Override
    public boolean addUserActionPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        synchronized (allowedUserActions){
            return allowedUserActions.add(action);
        }
    }

    @Override
    public boolean removeUserActionPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        synchronized (allowedUserActions){
            return allowedUserActions.remove(action);
        }
    }

    @Override
    public boolean addMarketActionPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        synchronized (allowedMarketActions){
            return allowedMarketActions.add(action);
        }
    }

    @Override
    public boolean removeMarketActionPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return false;
        }
        synchronized (allowedMarketActions){
            return allowedMarketActions.remove(action);
        }
    }

    @Override
    public boolean hasPermission(UserActions action) {
        if(defaultProfile.hasPermission(action)) {
            return true;
        }
        synchronized (allowedUserActions){
            return allowedUserActions.contains(action);
        }
    }

    @Override
    public boolean hasPermission(MarketActions action) {
        if(defaultProfile.hasPermission(action)) {
            return true;
        }
        synchronized (allowedMarketActions){
            return allowedMarketActions.contains(action);
        }
    }

    @Override
    public boolean hasPermission(String storeId, StoreActions action) {
        synchronized (storeIdToAllowedStoreActions){
            Set<StoreActions> allowedActions = storeIdToAllowedStoreActions.get(storeId);
            return allowedActions != null && allowedActions.contains(action);
        }
    }

    @Override
    public String getUserId() {
        return userId;
    }
}
