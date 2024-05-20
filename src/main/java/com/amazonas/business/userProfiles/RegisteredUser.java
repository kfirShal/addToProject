package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;

import java.util.Objects;

public class RegisteredUser extends User{

    private String userName;

    public String getPassword() {
        return password;
    }

    private String password;
    private String email;


    private boolean isAdmin;


    private Boolean isLoggedIn;

    public RegisteredUser(String id, String userName, String email, String password){
        super(id);
        if(Objects.equals(userName, "admin")){
            isAdmin = true;
        }
        this.isAdmin = false;
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.isLoggedIn = false;


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
    public void login(){
        isLoggedIn = true;
    }
    public void logout(){
        isLoggedIn = false;
    }

    @Override
    public ShoppingCart getCart() {
        return super.getCart();
    }
    public Boolean getLoggedIn() {
        return isLoggedIn;
    }

    @Override
    public String getUserId() {
        return super.getUserId();
    }

}

