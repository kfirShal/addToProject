package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.permissions.actions.UserActions;
import com.amazonas.business.userProfiles.*;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.exceptions.PurchaseFailedException;
import com.amazonas.exceptions.UserException;
import org.springframework.stereotype.Component;

@Component("userProxy")
public class UserProxy extends ControllerProxy {

    private final UsersController real;

    public UserProxy(UsersController usersController, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = usersController;
    }

    public void register(String email, String userName, String password) throws UserException {
        real.register(email, userName, password);
    }

    public String enterAsGuest() {
        return real.enterAsGuest();
    }

    //this is when the guest logs in to the market and turn to registeredUser
    public boolean loginToRegistered(String guestInitialId,String userId, String token) throws AuthenticationFailedException {
        authenticateToken(guestInitialId, token);
        return real.loginToRegistered(guestInitialId,userId);
    }

    public void logout(String userId, String token) throws AuthenticationFailedException {
        authenticateToken(userId, token);
        real.logout(userId);
    }

    public void logoutAsGuest(String guestInitialId, String token) throws AuthenticationFailedException {
        authenticateToken(guestInitialId, token);
        real.logoutAsGuest(guestInitialId);
    }


    public ShoppingCart getCart(String userId, String token) throws AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.VIEW_SHOPPING_CART);
        return real.getCart(userId);
    }


    public void addProductToCart(String userId, String storeName, String productId, int quantity, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.ADD_TO_SHOPPING_CART);
        real.addProductToCart(userId, storeName, productId, quantity);
    }


    public void RemoveProductFromCart(String userId,String storeName,String productId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.REMOVE_FROM_SHOPPING_CART);
        real.RemoveProductFromCart(userId, storeName, productId);
    }

    public void changeProductQuantity(String userId, String storeName, String productId, int quantity, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.UPDATE_SHOPPING_CART);
        real.changeProductQuantity(userId, storeName, productId, quantity);
    }

    public User getUser(String userId, String token) throws AuthenticationFailedException {
        authenticateToken(userId, token);
        return real.getUser(userId);
    }

    public void startPurchase(String userId, String token) throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.START_PURCHASE);
        real.startPurchase(userId);
    }

    public void payForPurchase(String userId, String token) throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.PAY_FOR_PURCHASE);
        real.payForPurchase(userId);
    }

    public void cancelPurchase(String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.CANCEL_PURCHASE);
        real.cancelPurchase(userId);
    }
}
