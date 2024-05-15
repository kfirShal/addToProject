package com.amazonas.business.permissions.profiles;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.userProfiles.UserActions;

import java.util.HashSet;
import java.util.Set;

public abstract class DefaultPermissionsProfile implements PermissionsProfile {

    private final String userId;
    private final Set<UserActions> allowedUserActions;
    private final Set<MarketActions> allowedMarketActions;
    private boolean updated;

    protected DefaultPermissionsProfile(String userId) {
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

    public boolean hasPermission(UserActions action) {
        return allowedUserActions.contains(action);
    }

    public boolean hasPermission(MarketActions action) {
        return allowedMarketActions.contains(action);
    }

    @Override
    public boolean hasPermission(String storeId, StoreActions action) {
        return false;
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
    public String getUserId(){
        return userId;
    }
}
