package com.amazonas.service;

import com.amazonas.business.permissions.proxies.UserProxy;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.common.exceptions.*;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import com.amazonas.service.requests.Request;
import com.amazonas.service.requests.users.CartRequest;
import com.amazonas.service.requests.users.LoginRequest;
import com.amazonas.service.requests.users.RegisterRequest;
import org.springframework.stereotype.Component;

@Component("userProfilesService")
public class UserProfilesService {

    private final UserProxy proxy;

    public UserProfilesService(UserProxy usersController) {
        this.proxy = usersController;
    }

    public String enterAsGuest(){


        try{
            String guestId = proxy.enterAsGuest();
            return new Response(true,guestId).toJson();
        } catch ( Exception e){
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String register(String json){
        Request request = Request.from(json);
        try{
            RegisterRequest toAdd = JsonUtils.deserialize(request.payload(), RegisterRequest.class);
            proxy.register(toAdd.email(), toAdd.userId(), toAdd.password());
            return new Response(true).toJson();
        } catch (UserException e){
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String loginToRegistered(String json){
        Request request = Request.from(json);
        try{
            LoginRequest toAdd = JsonUtils.deserialize(request.payload(), LoginRequest.class);
            ShoppingCart cart = proxy.loginToRegistered(toAdd.guestInitialId(), toAdd.userId(), request.token());
            return new Response(true,cart).toJson();
        } catch (AuthenticationFailedException | UserException e){
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String logout(String json){
        Request request = Request.from(json);
        try{
            String guestId = proxy.logout(request.userId(), request.token());
            return new Response(true,guestId).toJson();
        } catch (AuthenticationFailedException | UserException e){
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String logoutAsGuest(String json){
        Request request = Request.from(json);
        try{
            proxy.logoutAsGuest(request.userId(), request.token());
            return new Response(true).toJson();
        } catch (AuthenticationFailedException | UserException e){
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addProductToCart(String json){
        Request request = Request.from(json);
        try{
            CartRequest toAdd = JsonUtils.deserialize(request.payload(), CartRequest.class);
            proxy.addProductToCart(request.userId(),toAdd.storeId(), toAdd.productId(), toAdd.quantity(), request.token());
            return new Response(true).toJson();
        } catch (AuthenticationFailedException | NoPermissionException | ShoppingCartException | UserException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removeProductFromCart(String json) {
        Request request = Request.from(json);
        try {
            CartRequest toRemove = JsonUtils.deserialize(request.payload(), CartRequest.class);
            proxy.removeProductFromCart(request.userId(), toRemove.storeId(), toRemove.productId(), request.token());
            return new Response(true).toJson();
        } catch (AuthenticationFailedException | NoPermissionException | ShoppingCartException | UserException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String changeProductQuantity(String json) {
        Request request = Request.from(json);
        try {
            CartRequest toChange = JsonUtils.deserialize(request.payload(), CartRequest.class);
            proxy.changeProductQuantity(request.userId(), toChange.storeId(), toChange.productId(), toChange.quantity(), request.token());
            return new Response(true).toJson();
        } catch (AuthenticationFailedException | NoPermissionException | ShoppingCartException | UserException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String viewCart (String json){
        Request request = Request.from(json);
        try{
            ShoppingCart cart = proxy.viewCart(request.userId(), request.token());
            return new Response(true,cart).toJson();
        } catch (AuthenticationFailedException | NoPermissionException | UserException e){
            return Response.getErrorResponse(e).toJson();
        }

    }

    public String startPurchase(String json){
        Request request = Request.from(json);
        try{
            proxy.startPurchase(request.userId(), request.token());
            return new Response(true).toJson();
        } catch (AuthenticationFailedException | NoPermissionException | PurchaseFailedException | UserException e){
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String payForPurchase(String json){
        Request request = Request.from(json);
        try{
            proxy.payForPurchase(request.userId(), request.token());
            return new Response(true).toJson();
        } catch (AuthenticationFailedException | NoPermissionException | PurchaseFailedException | UserException e){
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String cancelPurchase(String json){
        Request request = Request.from(json);
        try{
            proxy.cancelPurchase(request.userId(), request.token());
            return new Response(true).toJson();
        } catch (AuthenticationFailedException | NoPermissionException | UserException e){
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String getUserTransactionHistory(String json){
        Request request = Request.from(json);
        try{
            return new Response(true,proxy.getUserTransactionHistory(request.userId(), request.token())).toJson();
        } catch (AuthenticationFailedException | NoPermissionException | UserException e){
            return Response.getErrorResponse(e).toJson();
        }
    }
}
