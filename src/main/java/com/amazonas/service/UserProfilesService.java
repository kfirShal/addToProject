package com.amazonas.service;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.UsersController;
import com.amazonas.utils.Response;
import org.springframework.stereotype.Component;

@Component
public class UserProfilesService {

    private final UsersController usersController;

    public UserProfilesService(UsersController controller) {
        this.usersController = controller;
    }

    public String register(String email, String userName, String password){
        try {
            usersController.register(email, userName, password);
            return new Response("User registered successfully", true).toJson();
        } catch (Exception e) {
            return Response.getErrorResponse(e).toJson();
        }

    }

    public String enterAsGuest(){
        try {
            usersController.enterAsGuest();
            return new Response("Entered as guest successfully", true).toJson();
        } catch (Exception e) {
            return Response.getErrorResponse(e).toJson();
        }
    }
    public String loginToRegistered(String guestInitialId,String userName){
        try {
            usersController.loginToRegistered(guestInitialId, userName);
            return new Response("Logged in successfully", true).toJson();
        } catch (Exception e) {
            return Response.getErrorResponse(e).toJson();
        }

    }
    public String logout(String userId){
        try {
            usersController.logout(userId);
            return new Response("Logged out successfully", true).toJson();
        } catch (Exception e) {
            return Response.getErrorResponse(e).toJson();
        }
    }
    public String logoutAsGuest(String guestInitialId){
        try {
            usersController.logoutAsGuest(guestInitialId);
            return new Response("Logged out as guest successfully", true).toJson();
        } catch (Exception e) {
            return Response.getErrorResponse(e).toJson();
        }
    }
    public String ViewCart(String userId){
        try {
            ShoppingCart cart = usersController.getCart(userId);
            return new Response("Cart found successfully", true, cart).toJson();
        } catch (Exception e) {
            return Response.getErrorResponse(e).toJson();
        }

    }
    public String addProductToCart(String userId, String storeName, Product product, int quantity){
        try {
            usersController.addProductToCart(userId, storeName, product, quantity);
            return new Response("Product added to cart successfully", true).toJson();
        } catch (Exception e) {
            return Response.getErrorResponse(e).toJson();
        }

    }
    public String RemoveProductFromCart(String userId,String storeName,String productId) {
        try {
            usersController.RemoveProductFromCart(userId, storeName, productId);
            return new Response("Product removed from cart successfully", true).toJson();
        } catch (Exception e) {
            return Response.getErrorResponse(e).toJson();
        }
    }
    public String changeProductQuantity(String userId, String storeName, String productId, int quantity) {
        try {
            usersController.changeProductQuantity(userId, storeName, productId, quantity);
            return new Response("Product quantity changed successfully", true).toJson();
        } catch (Exception e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

}
