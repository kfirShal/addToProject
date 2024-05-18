package com.amazonas.business.permissions.profiles;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.userProfiles.UserActions;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
public class DefaultPermissionsProfile implements PermissionsProfile {

    private final Set<UserActions> allowedUserActions;
    private final String userId;
    private final Set<MarketActions> allowedMarketActions;

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
        return false;
    }

    @Override
    public boolean removeUserActionPermission(UserActions action) {
        return false;
    }

    @Override
    public boolean addMarketActionPermission(MarketActions action) {
        return false;
    }

    @Override
    public boolean removeMarketActionPermission(MarketActions action) {
        return false;
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
    public String getUserId(){
        return userId;
    }
}
