package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;

public class RegisteredUser extends User{

    private String userName;
    private String password;
    private String email;
    private Boolean loggedIn;

    public RegisteredUser(int id, String userName, String email, String password){
        super(id);
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.loggedIn = false;

    }
    @Override
    public void addToCart(String storeName, int productId, Product product, int quantity) {
        super.addToCart(storeName, productId, product, quantity);
    }

    @Override
    public void getBasket(String storeName) {
        super.getBasket(storeName);
    }

    @Override
    public void removeProductFromBasket(String storeName, int productId) {
        super.removeProductFromBasket(storeName, productId);
    }

    @Override
    public Boolean isProductExists(String storeName, int productId) {
        return super.isProductExists(storeName, productId);
    }

    @Override
    public void changeProductQuantity(String storeName, int productId, int quantity) {
        super.changeProductQuantity(storeName, productId, quantity);
    }

    @Override
    public ShoppingCart getCart() {
        return super.getCart();
    }

    @Override
    public String getUserId() {
        return "";
    }

}

