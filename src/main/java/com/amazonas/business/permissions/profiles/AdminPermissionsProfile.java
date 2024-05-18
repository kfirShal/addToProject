package com.amazonas.business.permissions.profiles;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.userProfiles.UserActions;

public class AdminPermissionsProfile implements PermissionsProfile {

    private static final String USER_ID = "admin";

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
        return true;
    }

    @Override
    public boolean hasPermission(MarketActions action) {
        return true;
    }

    @Override
    public boolean hasPermission(String storeId, StoreActions action) {
        return true;
    }

    @Override
    public String getUserId() {
        return USER_ID;
    }
}
