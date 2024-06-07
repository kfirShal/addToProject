package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.permissions.actions.UserActions;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.business.userProfiles.*;
import com.amazonas.exceptions.*;
import org.springframework.stereotype.Component;

import java.util.List;

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
    public ShoppingCart loginToRegistered(String guestInitialId,String userId, String token) throws AuthenticationFailedException, UserException {
        authenticateToken(guestInitialId, token);
        return real.loginToRegistered(guestInitialId,userId);
    }

    public String logout(String userId, String token) throws AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        return real.logout(userId);
    }

    public void logoutAsGuest(String guestInitialId, String token) throws AuthenticationFailedException, UserException {
        authenticateToken(guestInitialId, token);
        real.logoutAsGuest(guestInitialId);
    }

    public void addProductToCart(String userId, String storeName, String productId, int quantity, String token) throws NoPermissionException, AuthenticationFailedException, ShoppingCartException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.ADD_TO_SHOPPING_CART);
        real.addProductToCart(userId, storeName, productId, quantity);
    }

    public void RemoveProductFromCart(String userId,String storeName,String productId, String token) throws NoPermissionException, AuthenticationFailedException, ShoppingCartException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.REMOVE_FROM_SHOPPING_CART);
        real.RemoveProductFromCart(userId, storeName, productId);
    }

    public void changeProductQuantity(String userId, String storeName, String productId, int quantity, String token) throws NoPermissionException, AuthenticationFailedException, ShoppingCartException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.UPDATE_SHOPPING_CART);
        real.changeProductQuantity(userId, storeName, productId, quantity);
    }

    public void startPurchase(String userId, String token) throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.START_PURCHASE);
        real.startPurchase(userId);
    }

    public void payForPurchase(String userId, String token) throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.PAY_FOR_PURCHASE);
        real.payForPurchase(userId);
    }

    public void cancelPurchase(String userId, String token) throws NoPermissionException, AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.CANCEL_PURCHASE);
        real.cancelPurchase(userId);
    }

    public List<Transaction> getUserTransactionHistory(String userId, String token) throws NoPermissionException, AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.VIEW_USER_TRANSACTIONS);
        return real.getUserTransactionHistory(userId);
    }
}
