package com.amazonas.business.userProfiles;

import java.util.Map;

public interface UsersController {

    public User getRegisteredUser(String userName);
    public User getGuest(String id);

    public void register(String email, String userName, String password);
    public void enterAsGuest();
    public void login(String userName, String password);
    public void returnToGuest();

    void logoutAsGuest();

    public StoreBasket getBasket();
    public ShoppingCart getCart();
    public void addProductToBasket();
    public void RemoveProductFromBasket();
    public void changeProductQuantity();
}
