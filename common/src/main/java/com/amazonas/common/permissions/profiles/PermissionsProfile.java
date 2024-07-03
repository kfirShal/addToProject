package com.amazonas.common.permissions.profiles;

import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.permissions.actions.UserActions;

public interface PermissionsProfile {

    boolean addStorePermission(String storeId, StoreActions action);

    boolean removeStorePermission(String storeId, StoreActions action);

    boolean addUserActionPermission(UserActions action);

    boolean removeUserActionPermission(UserActions action);

    boolean addMarketActionPermission(MarketActions action);

    boolean removeMarketActionPermission(MarketActions action);

    boolean hasPermission(UserActions action);

    boolean hasPermission(MarketActions action);

    boolean hasPermission(String storeId, StoreActions action);

    String getUserId();
}
