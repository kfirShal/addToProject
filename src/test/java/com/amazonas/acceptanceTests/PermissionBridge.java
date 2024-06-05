package com.amazonas.acceptanceTests;

import com.amazonas.business.permissions.actions.UserActions;

public interface PermissionBridge {
    boolean addPermission(String userId, UserActions action);
    boolean removePermission(String userId, UserActions action);
    boolean checkPermission(String userId, UserActions action);

    void testAddPermissionValid();

    void testAddPermissionInvalidUser();

    void testRemovePermissionValid();

    void testRemovePermissionInvalidUser();

    void testCheckPermissionValid();

    void testCheckPermissionInvalidUser();

    void testCheckPermissionNoPermission();
}
