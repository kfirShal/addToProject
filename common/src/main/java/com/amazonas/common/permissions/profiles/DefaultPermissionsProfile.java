package com.amazonas.common.permissions.profiles;

import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.permissions.actions.UserActions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class DefaultPermissionsProfile implements PermissionsProfile {

    private final Set<UserActions> allowedUserActions;
    private final Set<MarketActions> allowedMarketActions;
    private final String userId;

    public DefaultPermissionsProfile(String userId) {
        this.userId = userId;
        allowedUserActions = new HashSet<>();
        allowedMarketActions = new HashSet<>();
    }

    @Override
    public boolean addStorePermission(String storeId, StoreActions action) {
        return false;
    }

    @Override
    public boolean removeStorePermission(String storeId, StoreActions action) {
        return false;
    }

    @Override
    public boolean addUserActionPermission(UserActions action) {
        return allowedUserActions.add(action);
    }

    @Override
    public boolean removeUserActionPermission(UserActions action) {
        return allowedUserActions.remove(action);
    }

    @Override
    public boolean addMarketActionPermission(MarketActions action) {
        return allowedMarketActions.add(action);
    }

    @Override
    public boolean removeMarketActionPermission(MarketActions action) {
        return allowedMarketActions.remove(action);
    }

    @Override
    public boolean hasPermission(UserActions action) {
        return allowedUserActions.contains(action);
    }

    @Override
    public boolean hasPermission(MarketActions action) {
        return allowedMarketActions.contains(action);
    }

    @Override
    public boolean hasPermission(String storeId, StoreActions action) {
        return false;
    }

    @Override
    public List<String> getStoreIds() {
        return List.of();
    }

    @Override
    public String getUserId(){
        return userId;
    }
}
