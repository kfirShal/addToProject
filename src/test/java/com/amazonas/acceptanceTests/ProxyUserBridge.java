package com.amazonas.acceptanceTests;

public class ProxyUserBridge implements UserBridge {
    @Override
    public boolean register(String email, String userName, String password) {
        return false;
    }

    @Override
    public boolean enterAsGuest() {
        return false;
    }

    @Override
    public boolean login(String email, String password) {
        return false;
    }

    @Override
    public boolean logout() {
        return false;
    }

    @Override
    public boolean logoutAsGuest() {
        return false;
    }

    @Override
    public boolean getCart() {
        return false;
    }

    @Override
    public boolean addToCart(String productName, String productDescription) {
        return false;
    }

    @Override
    public boolean removeFromCart(String productName) {
        return false;
    }
}
