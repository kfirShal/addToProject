package com.amazonas.backend.business.permissions;

import com.amazonas.backend.business.suspended.SuspendedController;
import com.amazonas.backend.repository.PermissionsProfileRepository;
import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.permissions.actions.UserActions;
import com.amazonas.common.permissions.profiles.AdminPermissionsProfile;
import com.amazonas.common.permissions.profiles.DefaultPermissionsProfile;
import com.amazonas.common.permissions.profiles.PermissionsProfile;
import com.amazonas.common.permissions.profiles.UserPermissionsProfile;
import com.amazonas.common.utils.ReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings({"LoggingSimilarMessage", "BooleanMethodIsAlwaysInverted"})
@Component
public class PermissionsController {

    private static final Logger log = LoggerFactory.getLogger(PermissionsController.class);

    private final DefaultPermissionsProfile defaultProfile;
    private final DefaultPermissionsProfile guestProfile;
    private final AdminPermissionsProfile adminProfile;
    private final ReadWriteLock lock;
    private final PermissionsProfileRepository repository;
    private final SuspendedController suspendedController;
    private final List<MarketActions> allowedMarketActions;
    private final List<StoreActions> allowedStoreActions;
    private final List<UserActions> allowedUserActions;

    public PermissionsController(DefaultPermissionsProfile defaultRegisteredUserPermissionsProfile,
                                 DefaultPermissionsProfile guestPermissionsProfile,
                                 AdminPermissionsProfile adminPermissionsProfile,
                                 PermissionsProfileRepository permissionsProfileRepository, SuspendedController suspendedController) {
        defaultProfile = defaultRegisteredUserPermissionsProfile;
        guestProfile = guestPermissionsProfile;
        this.adminProfile = adminPermissionsProfile;
        this.repository = permissionsProfileRepository;
        this.suspendedController = suspendedController;
        lock = new ReadWriteLock();
        allowedMarketActions = new ArrayList<>(Arrays.asList(MarketActions.ALL, MarketActions.VIEW_PRODUCTS, MarketActions.VIEW_STORES, MarketActions.SEARCH_PRODUCTS, MarketActions.SEARCH_STORES));
        allowedStoreActions = new ArrayList<>(Arrays.asList(StoreActions.ALL, StoreActions.GET_PRODUCT_QUANTITY, StoreActions.VIEW_STORE_TRANSACTIONS, StoreActions.VIEW_ROLES_INFORMATION));
        allowedUserActions = new ArrayList<>(Arrays.asList(UserActions.ALL, UserActions.VIEW_SHOPPING_CART, UserActions.VIEW_USER_TRANSACTIONS));

    }

    //TODO: fix this when we have a database
    public boolean isAdmin(String userId) {
        log.debug("Checking if user {} is admin", userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile instanceof AdminPermissionsProfile;
        log.debug("User is {}", result ? "admin" : "not admin");
        return result;
    }

    public boolean addPermission(String userId, UserActions action) {
        log.debug("Adding action {} to user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.addUserActionPermission(action);
        log.debug("action was {}", result ? "added" : "not added");
        return result;
    }

    public boolean removePermission(String userId, UserActions action) {
        log.debug("Removing action {} from user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.removeUserActionPermission(action);
        log.debug("action was {}", result ? "removed" : "not removed");
        return result;
    }

    public boolean addPermission(String userId, String storeId, StoreActions action) {
        log.debug("Adding action {} to user {} for store {}", action, userId, storeId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.addStorePermission(storeId, action);
        log.debug("action was {}", result ? "added" : "not added");
        return result;
    }

    public boolean removePermission(String userId, String storeId, StoreActions action) {
        log.debug("Removing action {} from user {} for store {}", action, userId, storeId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.removeStorePermission(storeId, action);
        log.debug("action was {}", result ? "removed" : "not removed");
        return result;
    }

    public boolean addPermission(String userId, MarketActions action) {
        log.debug("Adding action {} to user {}", action, userId);
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.addMarketActionPermission(action);
        log.debug("action was {}", result ? "added" : "not added");
        return result;
    }

    public boolean checkPermission(String userId, UserActions action) {
        log.debug("Checking action {} for user {}", action, userId);
        if (isUserSuspended(userId) && !allowedUserActions.contains(action)) {
            log.debug("deny action {} for suspendedUser {}", action, userId);
            return false;
        }
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.hasPermission(action);
        log.debug("action is {}", result ? "granted" : "denied");
        return result;
    }

    public boolean checkPermission(String userId, String storeId, StoreActions action) {
        log.debug("Checking action {} for user {} for store {}", action, userId, storeId);
        if (isUserSuspended(userId) && !allowedStoreActions.contains(action)) {
            log.debug("deny action {} for suspendedUser {}", action, userId);

            return false;
        }
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.hasPermission(storeId, action);
        log.debug("action is {}", result ? "granted" : "denied");
        return result;
    }

    public boolean checkPermission(String userId, MarketActions action) {
        log.debug("Checking action {} for user {}", action, userId);
        if (isUserSuspended(userId) && !allowedMarketActions.contains(action)) {
            log.debug("deny action {} for suspendedUser {}", action, userId);

            return false;
        }
        PermissionsProfile profile = getPermissionsProfile(userId);
        boolean result = profile.hasPermission(action);
        log.debug("action is {}", result ? "granted" : "denied");
        return result;
    }

    private boolean isUserSuspended(String userId) {
        return suspendedController.isSuspended(userId);
    }

    public void registerUser(String userId) {
        log.debug("Registering user {}", userId);
        UserPermissionsProfile newProfile = new UserPermissionsProfile(userId, defaultProfile);
        registerUser(userId, newProfile, "User already registered");
    }

    public void registerGuest(String userId) {
        log.debug("Registering guest {}", userId);
        registerUser(userId, guestProfile, "Guest already registered");
    }

    public void registerAdmin(String userId) {
        log.debug("Registering admin {}", userId);
        registerUser(userId, adminProfile, "Admin already registered");
    }

    public void removeUser(String userId) {
        log.debug("Removing user {}", userId);
        removeUser(userId, "User not registered");
        log.debug("User removed successfully");
    }

    public void removeGuest(String userId) {
        log.debug("Removing guest {}", userId);
        removeUser(userId, "Guest not registered");
        log.debug("Guest removed successfully");
    }

    public void removeAdmin(String userId) {
        log.debug("Removing admin {}", userId);
        removeUser(userId, "Admin not registered");
        log.debug("Admin removed successfully");
    }

    private void registerUser(String userId, PermissionsProfile profile, String failMessage) {
        try {
            lock.acquireWrite();
            repository.addUser(userId, profile);
        } finally {
            lock.releaseWrite();
        }
    }

    private void removeUser(String userId, String failMessage) {
        try {
            lock.acquireWrite();
            var removed = repository.removeUser(userId);

            if (removed == null) {
                log.error(failMessage);
                throw new IllegalArgumentException(failMessage);
            }
        } finally {
            lock.releaseWrite();
        }
    }

    @NonNull
    public PermissionsProfile getPermissionsProfile(String userId) {
        log.trace("Fetching permissions profile for user {}", userId);
        lock.acquireRead();
        PermissionsProfile profile = repository.getPermissionsProfile(userId);
        lock.releaseRead();
        if (profile == null) {
            log.error("User not registered");
            throw new IllegalArgumentException("User not registered");
        }
        return profile;
    }

    public PermissionsProfile getGuestPermissionsProfile() {
        return guestProfile;
    }
}
