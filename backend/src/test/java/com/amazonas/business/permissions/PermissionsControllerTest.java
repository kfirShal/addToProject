package com.amazonas.business.permissions;

import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.permissions.actions.UserActions;
import com.amazonas.business.permissions.profiles.PermissionsProfile;
import com.amazonas.repository.PermissionsProfileRepository;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PermissionsControllerTest {

    private static final String USER_ID = "user1";
    private static final String STORE_ID = "store1";
    private static final UserActions USER_ACTION = UserActions.ADD_TO_SHOPPING_CART;
    private static final StoreActions STORE_ACTION = StoreActions.ADD_PRODUCT;
    private final PermissionsProfile profile;
    private final PermissionsController pc;

    public PermissionsControllerTest() {
        PermissionsProfileRepository repository = mock(PermissionsProfileRepository.class);
        PermissionsProfile mockProfile = mock(PermissionsProfile.class);
        pc = new PermissionsController(mockProfile, mockProfile, repository);
        profile = mock(PermissionsProfile.class);
        when(repository.getPermissionsProfile(USER_ID)).thenReturn(profile);
    }

    @Test
    void addSimplePermissionGood() {
        when(profile.addUserActionPermission(USER_ACTION)).thenReturn(true);
        assertTrue(pc.addPermission(USER_ID, USER_ACTION));
    }

    @Test
    void addSimplePermissionBad() {
        when(profile.addUserActionPermission(USER_ACTION)).thenReturn(false);
        assertFalse(pc.addPermission(USER_ID, USER_ACTION));
    }

    @Test
    void removeSimplePermissionGood() {
        when(profile.removeUserActionPermission(USER_ACTION)).thenReturn(true);
        assertTrue(pc.removePermission(USER_ID, USER_ACTION));
    }

    @Test
    void removeSimplePermissionBad() {
        when(profile.removeUserActionPermission(USER_ACTION)).thenReturn(false);
        assertFalse(pc.removePermission(USER_ID, USER_ACTION));
    }

    @Test
    void checkSimplePermission() {
        when(profile.hasPermission(USER_ACTION)).thenReturn(true);
        assertTrue(pc.checkPermission(USER_ID, USER_ACTION));
    }

    @Test
    void checkSimplePermissionBad() {
        when(profile.hasPermission(USER_ACTION)).thenReturn(false);
        assertFalse(pc.checkPermission(USER_ID, USER_ACTION));
    }

    @Test
    void addStorePermissionGood() {
        when(profile.addStorePermission(STORE_ID, STORE_ACTION)).thenReturn(true);
        assertTrue(pc.addPermission(USER_ID, STORE_ID, STORE_ACTION));
    }

    @Test
    void addStorePermissionBad() {
        when(profile.addStorePermission(STORE_ID, STORE_ACTION)).thenReturn(false);
        assertFalse(pc.addPermission(USER_ID, STORE_ID, STORE_ACTION));
    }

    @Test
    void removeStorePermissionGood() {
        when(profile.removeStorePermission(STORE_ID, STORE_ACTION)).thenReturn(true);
        assertTrue(pc.removePermission(USER_ID, STORE_ID, STORE_ACTION));
    }

    @Test
    void removeStorePermissionBad() {
        when(profile.removeStorePermission(STORE_ID, STORE_ACTION)).thenReturn(false);
        assertFalse(pc.removePermission(USER_ID, STORE_ID, STORE_ACTION));
    }

    @Test
    void checkStorePermission() {
        when(profile.hasPermission(STORE_ID, STORE_ACTION)).thenReturn(true);
        assertTrue(pc.checkPermission(USER_ID, STORE_ID, STORE_ACTION));
    }
}