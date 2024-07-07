package com.amazonas.common.permissions.profiles;

import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.permissions.actions.UserActions;

import java.util.List;

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
    public List<String> getStoreIds() {
        return List.of();
    }

    @Override
    public String getUserId() {
        return USER_ID;
    }
}
