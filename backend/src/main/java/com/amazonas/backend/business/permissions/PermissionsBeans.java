package com.amazonas.backend.business.permissions;

import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.UserActions;
import com.amazonas.common.permissions.profiles.AdminPermissionsProfile;
import com.amazonas.common.permissions.profiles.DefaultPermissionsProfile;
import com.amazonas.common.permissions.profiles.PermissionsProfile;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

@Configuration
public class PermissionsBeans {

    @Bean
    public PermissionsProfile guestPermissionsProfile() {
        return new DefaultPermissionsProfile("guest");
    }

    @Bean
    public PermissionsProfile defaultRegisteredUserPermissionsProfile() {
        return new DefaultPermissionsProfile("default_registered_user");
    }

    @Bean
    public PermissionsProfile adminPermissionsProfile() {
        return new AdminPermissionsProfile();
    }


    @EventListener
    public void handleApplicationReadyEvent(ApplicationReadyEvent event) {
        // build the permissions profiles
        buildGuestProfile();
        buildDefaultRegisteredUserProfile();
    }

    private void buildGuestProfile() {
        PermissionsProfile profile = guestPermissionsProfile();
        // shopping cart actions
        profile.addUserActionPermission(UserActions.ADD_TO_SHOPPING_CART);
        profile.addUserActionPermission(UserActions.REMOVE_FROM_SHOPPING_CART);
        profile.addUserActionPermission(UserActions.UPDATE_SHOPPING_CART);
        profile.addUserActionPermission(UserActions.VIEW_SHOPPING_CART);
        // purchase actions
        profile.addUserActionPermission(UserActions.START_PURCHASE);
        profile.addUserActionPermission(UserActions.CANCEL_PURCHASE);
        profile.addUserActionPermission(UserActions.PAY_FOR_PURCHASE);
        // market actions
        profile.addMarketActionPermission(MarketActions.SEARCH_PRODUCTS);
    }

    private void buildDefaultRegisteredUserProfile() {
        PermissionsProfile profile = defaultRegisteredUserPermissionsProfile();
        // shopping cart actions
        profile.addUserActionPermission(UserActions.ADD_TO_SHOPPING_CART);
        profile.addUserActionPermission(UserActions.REMOVE_FROM_SHOPPING_CART);
        profile.addUserActionPermission(UserActions.UPDATE_SHOPPING_CART);
        profile.addUserActionPermission(UserActions.VIEW_SHOPPING_CART);
        // purchase actions
        profile.addUserActionPermission(UserActions.START_PURCHASE);
        profile.addUserActionPermission(UserActions.CANCEL_PURCHASE);
        profile.addUserActionPermission(UserActions.PAY_FOR_PURCHASE);
        profile.addUserActionPermission(UserActions.VIEW_USER_TRANSACTIONS);
        // market actions
        profile.addMarketActionPermission(MarketActions.SEARCH_PRODUCTS);
    }
}
