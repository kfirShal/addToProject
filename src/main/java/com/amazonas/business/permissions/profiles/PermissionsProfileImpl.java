package com.amazonas.business.permissions.profiles;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.userProfiles.UserActions;
import com.amazonas.business.stores.StoreActions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class PermissionsProfileImpl implements PermissionsProfile {

    private static final DefaultRegisteredUserPermissionsProfile defaultProfile = DefaultRegisteredUserPermissionsProfile.getInstance();

    private final String userId;

    private final Map<String,Set<StoreActions>> storeIdToAllowedStoreActions;
    private final Set<UserActions> allowedUserActions;
    private final Set<MarketActions> allowedMarketActions;
    private boolean updated;

    public PermissionsProfileImpl(String userId) {
        this.userId = userId;
        storeIdToAllowedStoreActions = new HashMap<>();
        allowedUserActions = new HashSet<>();
        allowedMarketActions = new HashSet<>();
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
        return allowedActions.remove(action);
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
        return defaultProfile.hasPermission(action) || allowedUserActions.contains(action);
    }

    @Override
    public boolean hasPermission(MarketActions action) {
        return defaultProfile.hasPermission(action) || allowedMarketActions.contains(action);
    }

    @Override
    public boolean hasPermission(String storeId, StoreActions action) {
        Set<StoreActions> allowedActions = storeIdToAllowedStoreActions.get(storeId);
        return allowedActions != null && allowedActions.contains(action);
    }

    @Override
    public boolean updated() {
        return updated;
    }

    @Override
    public void setUpdated() {
        updated = true;
    }

    @Override
    public String getUserId() {
        return userId;
    }
}
