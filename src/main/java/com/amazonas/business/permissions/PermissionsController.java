package com.amazonas.business.permissions;

import com.amazonas.business.market.MarketActions;
import com.amazonas.business.permissions.profiles.PermissionsProfile;
import com.amazonas.business.permissions.profiles.RegisteredUserPermissionsProfile;
import com.amazonas.business.stores.StoreActions;
import com.amazonas.business.userProfiles.UserActions;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PermissionsController {

    private final PermissionsProfile defaultProfile;
    private final PermissionsProfile guestProfile;
    private final Map<String, PermissionsProfile> userIdToPermissionsProfile;

    public PermissionsController(PermissionsProfile defaultRegisteredUserPermissionsProfile,
                                 PermissionsProfile guestPermissionsProfile) {
        userIdToPermissionsProfile = new HashMap<>();
        defaultProfile = defaultRegisteredUserPermissionsProfile;
        guestProfile = guestPermissionsProfile;
    }
    
    public boolean addPermission(String userId, UserActions action) {
        PermissionsProfile profile = userIdToPermissionsProfile.get(userId);
        if (profile == null) {
            return false;
        }
        if(profile.addUserActionPermission(action)) {
            profile.setUpdated();
            return true;
        }
        return false;
    }

    public boolean removePermission(String userId, UserActions action) {
        PermissionsProfile profile = userIdToPermissionsProfile.get(userId);
        if (profile == null) {
            return false;
        }
        if(profile.removeUserActionPermission(action)) {
            profile.setUpdated();
            return true;
        }
        return false;
    }
    
    public boolean addPermission(String userId, String storeId, StoreActions action) {
        PermissionsProfile profile = userIdToPermissionsProfile.get(userId);
        if (profile == null) {
            return false;
        }
        if(profile.addStorePermission(storeId, action)) {
            profile.setUpdated();
            return true;
        }
        return false;
    }
    
    public boolean removePermission(String userId, String storeId, StoreActions action) {
        PermissionsProfile profile = userIdToPermissionsProfile.get(userId);
        if (profile == null) {
            return false;
        }
        if(profile.removeStorePermission(storeId, action)) {
            profile.setUpdated();
            return true;
        }
        return false;
    }
    
    public boolean addPermission(String userId, String marketId, MarketActions action) {
        PermissionsProfile profile = userIdToPermissionsProfile.get(userId);
        if (profile == null) {
            return false;
        }
        if(profile.addMarketActionPermission(action)) {
            profile.setUpdated();
            return true;
        }
        return false;
    }
    
    public boolean checkPermission(String userId, UserActions action) {
        PermissionsProfile profile = userIdToPermissionsProfile.get(userId);
        if (profile == null) {
            return false;
        }
        return profile.hasPermission(action);
    }
    
    public boolean checkPermission(String userId, String storeId, StoreActions action) {
        PermissionsProfile profile = userIdToPermissionsProfile.get(userId);
        if (profile == null) {
            return false;
        }
        return profile.hasPermission(storeId, action);
    }
    
    public boolean checkPermission(String userId, MarketActions action) {
        PermissionsProfile profile = userIdToPermissionsProfile.get(userId);
        if (profile == null) {
            return false;
        }
        return profile.hasPermission(action);
    }

    public void registerUser(String userId) {
        RegisteredUserPermissionsProfile newProfile = new RegisteredUserPermissionsProfile(userId, defaultProfile);
        newProfile.setUpdated();
        userIdToPermissionsProfile.put(userId, newProfile);
    }

    public void registerGuest(String userId) {
        userIdToPermissionsProfile.put(userId, guestProfile);
    }
}
