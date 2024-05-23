package com.amazonas.acceptanceTests;

import com.amazonas.business.userProfiles.UserActions;

public interface PermissionBridge {
    boolean addPermission(String userId, UserActions action);
    boolean removePermission(String userId, UserActions action);
    boolean checkPermission(String userId, UserActions action);

}
