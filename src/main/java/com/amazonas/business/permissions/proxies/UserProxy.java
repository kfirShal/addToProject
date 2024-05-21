package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.UsersController;
import org.springframework.stereotype.Component;

@Component("userProxy")
public class UserProxy extends ControllerProxy implements UsersController {

    private final UsersController real;

    public UserProxy(UsersController usersController, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = usersController;
    }

    @Override
    public void register(String email, String userName, String password) {

    }

    @Override
    public void enterAsGuest() {

    }

    @Override
    public void login(String userName) {
        //add user to logged in users
    }

    @Override
    public void logout() {

    }

    @Override
    public void logoutAsGuest() {

    }

    @Override
    public ShoppingCart getCart() {
        return null;
    }

    @Override
    public void addProductToCart() {

    }

    @Override
    public void RemoveProductFromCart() {

    }

    @Override
    public void changeProductQuantity() {

    }
}
