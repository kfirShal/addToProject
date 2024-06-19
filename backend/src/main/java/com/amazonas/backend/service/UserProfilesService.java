package com.amazonas.backend.service;

import com.amazonas.backend.business.authentication.UserCredentials;
import com.amazonas.backend.business.permissions.proxies.UserProxy;
import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.exceptions.*;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.users.CartRequest;
import com.amazonas.common.requests.users.LoginRequest;
import com.amazonas.common.requests.users.RegisterRequest;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component("userProfilesService")
public class UserProfilesService {

    private final UserProxy proxy;

    public UserProfilesService(UserProxy usersController) {
        this.proxy = usersController;
    }

    public String enterAsGuest(){
        String guestId = proxy.enterAsGuest();
        return Response.getOk(guestId);
    }

    public String register(String json){
        Request request = Request.from(json);
        try{
            RegisterRequest toAdd = JsonUtils.deserialize(request.payload(), RegisterRequest.class);
            proxy.register(toAdd.email(), toAdd.userid(), toAdd.password(), request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String loginToRegistered(String json){
        Request request = Request.from(json);
        try{
            LoginRequest toAdd = JsonUtils.deserialize(request.payload(), LoginRequest.class);
            proxy.loginToRegistered(toAdd.guestInitialId(), toAdd.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String logout(String json){
        Request request = Request.from(json);
        try{
            proxy.logout(request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String logoutAsGuest(String json){
        Request request = Request.from(json);
        try{
            proxy.logoutAsGuest(request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String addProductToCart(String json){
        Request request = Request.from(json);
        try{
            CartRequest toAdd = JsonUtils.deserialize(request.payload(), CartRequest.class);
            proxy.addProductToCart(request.userId(),toAdd.storeId(), toAdd.productId(), toAdd.quantity(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | ShoppingCartException | UserException e) {
            return Response.getError(e);
        }
    }

    public String removeProductFromCart(String json) {
        Request request = Request.from(json);
        try {
            CartRequest toRemove = JsonUtils.deserialize(request.payload(), CartRequest.class);
            proxy.removeProductFromCart(request.userId(), toRemove.storeId(), toRemove.productId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | ShoppingCartException | UserException e) {
            return Response.getError(e);
        }
    }

    public String changeProductQuantity(String json) {
        Request request = Request.from(json);
        try {
            CartRequest toChange = JsonUtils.deserialize(request.payload(), CartRequest.class);
            proxy.changeProductQuantity(request.userId(), toChange.storeId(), toChange.productId(), toChange.quantity(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | ShoppingCartException | UserException e) {
            return Response.getError(e);
        }
    }

    public String viewCart(String json){
        Request request = Request.from(json);
        try{
            ShoppingCart cart = proxy.viewCart(request.userId(), request.token());
            return Response.getOk(cart);
        } catch (AuthenticationFailedException | NoPermissionException | UserException e){
            return Response.getError(e);
        }

    }

    public String startPurchase(String json){
        Request request = Request.from(json);
        try{
            proxy.startPurchase(request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | PurchaseFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String payForPurchase(String json){
        Request request = Request.from(json);
        try{
            proxy.payForPurchase(request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | PurchaseFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String cancelPurchase(String json){
        Request request = Request.from(json);
        try{
            proxy.cancelPurchase(request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | UserException e){
            return Response.getError(e);
        }
    }

    public String getUserTransactionHistory(String json){
        Request request = Request.from(json);
        try{
            return Response.getOk(proxy.getUserTransactionHistory(request.userId(), request.token()));
        } catch (AuthenticationFailedException | NoPermissionException | UserException e){
            return Response.getError(e);
        }
    }
}
