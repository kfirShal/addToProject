package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.authentication.UserCredentials;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.common.dtos.UserInformation;
import com.amazonas.common.permissions.actions.UserActions;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.business.userProfiles.UsersController;
import com.amazonas.backend.exceptions.*;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component("userProxy")
public class UserProxy extends ControllerProxy {

    private final UsersController real;

    public UserProxy(UsersController usersController, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = usersController;
    }

    public void register(String email, String userName, String password, LocalDate birthDate, String guestId, String token) throws UserException, AuthenticationFailedException {
        if(userName.equals("admin")) {
            throw new UserException();
        }
        authenticateToken(guestId, token);
        real.register(email, userName, password, birthDate);
    }

    public String enterAsGuest() {
        return real.enterAsGuest();
    }

    //this is when the guest logs in to the market and turn to registeredUser
    public void loginToRegistered(String guestInitialId,String userId, String token) throws AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        real.loginToRegistered(guestInitialId,userId);
    }

    public void logout(String userId, String token) throws AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        real.logout(userId);
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

    public void removeProductFromCart(String userId,String storeName,String productId, String token) throws NoPermissionException, AuthenticationFailedException, ShoppingCartException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.REMOVE_FROM_SHOPPING_CART);
        real.removeProductFromCart(userId, storeName, productId);
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
    public ShoppingCart viewCart(String userId, String token) throws NoPermissionException, AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.VIEW_SHOPPING_CART);
        return real.viewCart(userId);
    }

    public List<Transaction> getUserTransactionHistory(String userId, String token) throws NoPermissionException, AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        checkPermission(userId, UserActions.VIEW_USER_TRANSACTIONS);
        return real.getUserTransactionHistory(userId);
    }

    public UserInformation getUserInformation(String userId, String token, String requestedUserId) throws AuthenticationFailedException, UserException {
        authenticateToken(userId, token);
        return real.getUserInformation(requestedUserId);
    }
}
