package com.amazonas.business.permissions.profiles;

import com.amazonas.business.permissions.actions.MarketActions;
import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.permissions.actions.UserActions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.TestComponent;

import static org.junit.jupiter.api.Assertions.*;

class RegisteredUserPermissionsProfileTest {

    private static final String USER_ID = "user1";
    private static final String STORE_ID = "store1";
    private static final UserActions USER_ACTION = UserActions.ADD_TO_SHOPPING_CART;
    private static final StoreActions STORE_ACTION = StoreActions.ADD_PRODUCT;
    private static final MarketActions MARKET_ACTION = MarketActions.CREATE_STORE;
    private PermissionsProfile profile;

    @BeforeEach
    void setUp() {
        profile = new RegisteredUserPermissionsProfile(USER_ID, new DefaultPermissionsProfile("default_registered_user"));
    }

    @Test
    void addStorePermissionGood() {
        assertTrue(profile.addStorePermission(STORE_ID, STORE_ACTION));
        assertTrue(profile.hasPermission(STORE_ID, STORE_ACTION));
    }

    @Test
    void removeStorePermissionGood() {
        assertTrue(profile.addStorePermission(STORE_ID, STORE_ACTION));
        assertTrue(profile.hasPermission(STORE_ID, STORE_ACTION));
        assertTrue(profile.removeStorePermission(STORE_ID, STORE_ACTION));
        assertFalse(profile.hasPermission(STORE_ID, STORE_ACTION));
    }

    @Test
    void removeStorePermissionBad() {
        assertFalse(profile.removeStorePermission(STORE_ID, STORE_ACTION));
    }

    @Test
    void addUserActionPermissionGood() {
        assertTrue(profile.addUserActionPermission(USER_ACTION));
        assertTrue(profile.hasPermission(USER_ACTION));
    }

    @Test
    void removeUserActionPermissionGood() {
        assertTrue(profile.addUserActionPermission(USER_ACTION));
        assertTrue(profile.hasPermission(USER_ACTION));
        assertTrue(profile.removeUserActionPermission(USER_ACTION));
        assertFalse(profile.hasPermission(USER_ACTION));
    }

    @Test
    void removeUserActionPermissionBad() {
        assertFalse(profile.removeUserActionPermission(USER_ACTION));
    }

    @Test
    void addMarketActionPermissionGood() {
        assertTrue(profile.addMarketActionPermission(MARKET_ACTION));
        assertTrue(profile.hasPermission(MARKET_ACTION));
    }

    @Test
    void removeMarketActionPermissionGood() {
        assertTrue(profile.addMarketActionPermission(MARKET_ACTION));
        assertTrue(profile.hasPermission(MARKET_ACTION));
        assertTrue(profile.removeMarketActionPermission(MARKET_ACTION));
        assertFalse(profile.hasPermission(MARKET_ACTION));
    }

    @Test
    void hasPermissionGood() {
        assertTrue(profile.addStorePermission(STORE_ID, STORE_ACTION));
        assertTrue(profile.addMarketActionPermission(MARKET_ACTION));
        assertTrue(profile.addUserActionPermission(USER_ACTION));
        assertTrue(profile.hasPermission(STORE_ID, STORE_ACTION));
        assertTrue(profile.hasPermission(MARKET_ACTION));
        assertTrue(profile.hasPermission(USER_ACTION));
    }

    @Test
    void hasPermissionBad() {
        assertFalse(profile.hasPermission(STORE_ID, STORE_ACTION));
        assertFalse(profile.hasPermission(MARKET_ACTION));
        assertFalse(profile.hasPermission(USER_ACTION));
    }

    @Test
    void getUserId() {
        assertEquals(USER_ID, profile.getUserId());
    }
}