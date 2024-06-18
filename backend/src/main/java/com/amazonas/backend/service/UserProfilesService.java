package com.amazonas.backend.service;

import com.amazonas.backend.business.permissions.proxies.UserProxy;
import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import com.amazonas.backend.exceptions.*;
import com.amazonas.backend.service.requests.Request;
import com.amazonas.backend.service.requests.users.CartRequest;
import com.amazonas.backend.service.requests.users.LoginRequest;
import com.amazonas.backend.service.requests.users.RegisterRequest;
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
            proxy.register(toAdd.email(), toAdd.userid(), toAdd.password(), request.userid(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String loginToRegistered(String json){
        Request request = Request.from(json);
        try{
            LoginRequest toAdd = JsonUtils.deserialize(request.payload(), LoginRequest.class);
            ShoppingCart cart = proxy.loginToRegistered(toAdd.guestInitialId(), toAdd.userId(), request.token());
            return Response.getOk(cart);
        } catch (AuthenticationFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String logout(String json){
        Request request = Request.from(json);
        try{
            String guestId = proxy.logout(request.userid(), request.token());
            return Response.getOk(guestId);
        } catch (AuthenticationFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String logoutAsGuest(String json){
        Request request = Request.from(json);
        try{
            proxy.logoutAsGuest(request.userid(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String addProductToCart(String json){
        Request request = Request.from(json);
        try{
            CartRequest toAdd = JsonUtils.deserialize(request.payload(), CartRequest.class);
            proxy.addProductToCart(request.userid(),toAdd.storeId(), toAdd.productId(), toAdd.quantity(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | ShoppingCartException | UserException e) {
            return Response.getError(e);
        }
    }

    public String removeProductFromCart(String json) {
        Request request = Request.from(json);
        try {
            CartRequest toRemove = JsonUtils.deserialize(request.payload(), CartRequest.class);
            proxy.removeProductFromCart(request.userid(), toRemove.storeId(), toRemove.productId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | ShoppingCartException | UserException e) {
            return Response.getError(e);
        }
    }

    public String changeProductQuantity(String json) {
        Request request = Request.from(json);
        try {
            CartRequest toChange = JsonUtils.deserialize(request.payload(), CartRequest.class);
            proxy.changeProductQuantity(request.userid(), toChange.storeId(), toChange.productId(), toChange.quantity(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | ShoppingCartException | UserException e) {
            return Response.getError(e);
        }
    }

    public String viewCart(String json){
        Request request = Request.from(json);
        try{
            ShoppingCart cart = proxy.viewCart(request.userid(), request.token());
            return Response.getOk(cart);
        } catch (AuthenticationFailedException | NoPermissionException | UserException e){
            return Response.getError(e);
        }

    }

    public String startPurchase(String json){
        Request request = Request.from(json);
        try{
            proxy.startPurchase(request.userid(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | PurchaseFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String payForPurchase(String json){
        Request request = Request.from(json);
        try{
            proxy.payForPurchase(request.userid(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | PurchaseFailedException | UserException e){
            return Response.getError(e);
        }
    }

    public String cancelPurchase(String json){
        Request request = Request.from(json);
        try{
            proxy.cancelPurchase(request.userid(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | NoPermissionException | UserException e){
            return Response.getError(e);
        }
    }

    public String getUserTransactionHistory(String json){
        Request request = Request.from(json);
        try{
            return Response.getOk(proxy.getUserTransactionHistory(request.userid(), request.token()));
        } catch (AuthenticationFailedException | NoPermissionException | UserException e){
            return Response.getError(e);
        }
    }
}
