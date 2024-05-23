package com.amazonas.acceptanceTests;

public interface UserBridge {
    boolean register(String email, String userName, String password);
    boolean enterAsGuest();
    boolean login(String email, String password);
    boolean logout();
    boolean logoutAsGuest();
    boolean getCart();
    boolean addToCart(String productName, String productDescription);
    boolean removeFromCart(String productName);

}
