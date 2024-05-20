package com.amazonas.business.userProfiles;

public interface UsersController {

    void register(String email, String userName, String password);

    void enterAsGuest();

    void login(String userName);

    void logout();

    void logoutAsGuest();

    ShoppingCart getCart();

    void addProductToCart();

    void RemoveProductFromCart();

    void changeProductQuantity();
}
