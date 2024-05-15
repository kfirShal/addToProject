package com.amazonas.business.permissions.profiles;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.market.MarketActions;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.userProfiles.UserActions;
import com.amazonas.business.stores.StoreActions;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DefaultRegisteredUserPermissionsProfile extends DefaultPermissionsProfile {

    private static final String USER_ID = "default_registered_user";
    private static DefaultRegisteredUserPermissionsProfile instance;

    private DefaultRegisteredUserPermissionsProfile(String userId) {
        super(userId);
    }

    public static DefaultRegisteredUserPermissionsProfile getInstance() {
        if (instance == null) {
            instance = new DefaultRegisteredUserPermissionsProfile(USER_ID);
        }
        return instance;
    }
}
