package com.amazonas.business.permissions.profiles;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.userProfiles.UserActions;

import java.util.HashSet;
import java.util.Set;

public class GuestPermissionsProfile extends DefaultPermissionsProfile {

    private static final String USER_ID = "guest";
    private static GuestPermissionsProfile instance;

    private GuestPermissionsProfile(String userId) {
        super(userId);
    }

    public static GuestPermissionsProfile getInstance() {
        if (instance == null) {
            instance = new GuestPermissionsProfile(USER_ID);
        }
        return instance;
    }
}
