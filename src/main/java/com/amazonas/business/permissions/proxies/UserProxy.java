package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.StoreBasket;
import com.amazonas.business.userProfiles.User;
import com.amazonas.business.userProfiles.UsersController;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("userProxy")
public class UserProxy extends ControllerProxy implements UsersController {

    private final UsersController real;

    public UserProxy(UsersController usersController, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = usersController;
    }

    @Override
    public User getRegisteredUser(String userName) {
        return null;
    }

    @Override
    public User getGuest(String id) {
        return null;
    }

    @Override
    public void register(String email, String userName, String password) {

    }

    @Override
    public void enterAsGuest() {

    }

    @Override
    public void login(String userName, String password) {

    }

    @Override
    public void returnToGuest() {

    }

    @Override
    public void logoutAsGuest() {

    }

    @Override
    public StoreBasket getBasket() {
        return null;
    }

    @Override
    public ShoppingCart getCart() {
        return null;
    }

    @Override
    public void addProductToBasket() {

    }

    @Override
    public void RemoveProductFromBasket() {

    }

    @Override
    public void changeProductQuantity() {

    }
}
