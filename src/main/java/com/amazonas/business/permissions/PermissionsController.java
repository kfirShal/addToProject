package com.amazonas.business.permissions;

import com.amazonas.business.userProfiles.UserActions;
import com.amazonas.business.userProfiles.User;

public class PermissionsController {
    public boolean addPermission(User user, UserActions action) {
        return false;
    }

    public boolean removePermission(User user, UserActions action) {
        return false;
    }

    public boolean checkPermission(User user, UserActions action) {
        return false;
    }

    //TODO: Add different types of permissions
}
