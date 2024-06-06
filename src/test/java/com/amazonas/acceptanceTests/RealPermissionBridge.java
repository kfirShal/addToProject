package com.amazonas.acceptanceTests;

import com.amazonas.business.permissions.actions.UserActions;

public class RealPermissionBridge implements PermissionBridge {
    @Override
    public boolean addPermission(String userId, UserActions action) {
        return false;
    }

    @Override
    public boolean removePermission(String userId, UserActions action) {
        return false;
    }

    @Override
    public boolean checkPermission(String userId, UserActions action) {
        return false;
    }
}
