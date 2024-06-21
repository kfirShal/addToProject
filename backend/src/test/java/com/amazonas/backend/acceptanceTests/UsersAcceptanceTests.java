package com.amazonas.backend.acceptanceTests;

import com.amazonas.backend.business.permissions.proxies.UserProxy;
import com.amazonas.backend.business.userProfiles.ShoppingCart;
import com.amazonas.backend.exceptions.*;
import com.amazonas.backend.service.UserProfilesService;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.users.CartRequest;
import com.amazonas.common.requests.users.LoginRequest;
import com.amazonas.common.requests.users.RegisterRequest;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UsersAcceptanceTests {
    @Mock
    private UserProxy usersController;
    private ShoppingCart shoppingCart;
    @InjectMocks
    private UserProfilesService userProfilesService;
    @BeforeEach
    void setUp() {
        // Manually create a mock for UsersController
        usersController = mock(UserProxy.class);
        shoppingCart = mock(ShoppingCart.class);
        userProfilesService = new UserProfilesService(usersController);
    }


//-------------------------UseCase 2.1.1-------------------------
    @Test
    void EnterAsGuestSuccess() {

        when(usersController.enterAsGuest()).thenReturn("guestId");
        String result = userProfilesService.enterAsGuest();
        assertEquals(Response.getOk("guestId"), result);


    }

//-------------------------UseCase 2.1.2-------------------------
    @Test
    void logoutAsGuestSuccess() throws AuthenticationFailedException, UserException {
        when(usersController.enterAsGuest()).thenReturn("guestId");
        userProfilesService.enterAsGuest();
        Request request = new Request("guestId","token","");
        doNothing().when(usersController).logoutAsGuest(request.userId(),request.token());
        String result = userProfilesService.logoutAsGuest(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void logoutAsGuestFailureGuestNotExists() throws AuthenticationFailedException, UserException {
        Request request = new Request("NotGuestId","token","");
        doThrow(new UserException("Failed to logout as guest")).when(usersController).logoutAsGuest(request.userId(),request.token());
        String result = userProfilesService.logoutAsGuest(request.toJson());
        assertEquals(Response.getError(new UserException("Failed to logout as guest")), result);
    }


//-------------------------UseCase 2.1.3-------------------------
    @Test
    void testRegisterSuccess() throws UserException, AuthenticationFailedException {

        String email = "test@example.com";
        String userName = "testUser";
        String password = "password123@";
        doNothing().when(usersController).register(email, userName, password,"","");
        RegisterRequest registerRequest = new RegisterRequest(email, userName, password);
        Request request = new Request("userId","token",JsonUtils.serialize(registerRequest));
        String result = userProfilesService.register(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void testRegisterFailureInvalidPassword() throws UserException, AuthenticationFailedException {

        String email = "test@example.com";
        String userName = "testUser";
        String password = "password";
        doThrow(new UserException("Registration failed - Illegal Password")).when(usersController).register(email, userName, password,"","");
        RegisterRequest registerRequest = new RegisterRequest(email, userName, password);
        Request request = new Request("userId","token",JsonUtils.serialize(registerRequest));
        String result = userProfilesService.register(request.toJson());
        assertEquals(Response.getError(new UserException("Registration failed - Illegal Password")), result);
    }
    @Test
    void testRegisterFailureInvalidEmail() throws UserException, AuthenticationFailedException {

        String email = "test@example";
        String userName = "testUser";
        String password = "password";
        doThrow(new UserException("Invalid email address.")).when(usersController).register(email, userName, password,"","");
        RegisterRequest registerRequest = new RegisterRequest(email, userName, password);
        Request request = new Request("userId","token",JsonUtils.serialize(registerRequest));
        String result = userProfilesService.register(request.toJson());
        assertEquals(Response.getError(new UserException("Invalid email address.")), result);
    }
    @Test
    void testRegisterFailureUsernameAlreadyExists() throws UserException, AuthenticationFailedException {

        String email = "test@example.com";
        String userName = "existsTestUser";
        String password = "password";
        RegisterRequest registerRequest = new RegisterRequest(email, userName, password);
        Request request = new Request("userId","token",JsonUtils.serialize(registerRequest));
        userProfilesService.register(request.toJson());
        doThrow(new UserException("Registration failed - Username Already Exists"))
                .when(usersController).register(email, userName, password,"","");
        String result = userProfilesService.register(request.toJson());
        assertEquals(Response.getError(new UserException("Registration failed - Username Already Exists")), result);
    }
//-------------------------UseCase 2.1.4-------------------------
    @Test
    void testLoginToRegisteredSuccess() throws AuthenticationFailedException, UserException {

        String guestId = "guestId";
        String userName = "testUser";
        LoginRequest loginRequest = new LoginRequest(guestId, userName);
        Request request = new Request("userId","token",JsonUtils.serialize(loginRequest));
        String result = userProfilesService.loginToRegistered(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void testLoginToRegisteredFailureWrongUsername() throws UserException, AuthenticationFailedException {

        String guestId = "guestId";
        String userName = "testUser43"; // instead of "testUser" -- the correct userId
        LoginRequest loginRequest = new LoginRequest(guestId, userName);
        Request request = new Request("userId","token",JsonUtils.serialize(loginRequest));
        doThrow(new UserException("Failed to login - Wrong userId")).when(usersController).loginToRegistered(guestId, userName,request.token());
        String result = userProfilesService.loginToRegistered(request.toJson());
        assertEquals(Response.getError(new UserException("Failed to login - Wrong userId")), result);
    }

//---------------------------UseCase 2.2.3-------------------------
    @Test
    void addProductToCartSuccess() throws AuthenticationFailedException, NoPermissionException, ShoppingCartException, UserException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        CartRequest cartRequest = new CartRequest(storeName, productId, 1);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        doNothing().when(usersController).addProductToCart(userId, storeName, productId, 1,request.token());
        String result = userProfilesService.addProductToCart(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void addProductToCartFailureProductOutOfStock() throws AuthenticationFailedException, NoPermissionException, ShoppingCartException, UserException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        CartRequest cartRequest = new CartRequest(storeName, productId, 1);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        doThrow(new ShoppingCartException("Failed to add product to cart - the product is out of stock")).when(usersController).addProductToCart(userId, storeName, productId, 1,request.token());
        String result = userProfilesService.addProductToCart(request.toJson());
        assertEquals(Response.getError(new ShoppingCartException("Failed to add product to cart - the product is out of stock")), result);
    }
//---------------------------UseCase 2.2.4-------------------------
    @Test
    void testViewCartSuccess() throws AuthenticationFailedException, NoPermissionException, UserException {

        String userId = "userId";
        Request request = new Request(userId,"token","");
        when(usersController.viewCart(request.userId(),request.token())).thenReturn(shoppingCart);
        String result = userProfilesService.viewCart(request.toJson());
        assertEquals(Response.getOk(shoppingCart), result);
    }

    @Test
    void testViewCartFailureWrongId() throws AuthenticationFailedException, NoPermissionException, UserException {

        String userId = "wrongId";
        Request request = new Request(userId,"token","");
        doThrow(new UserException("Failed to find cart - Wrong ID")).when(usersController).viewCart(request.userId(),request.token());
        String result = userProfilesService.viewCart(request.toJson());
        assertEquals(Response.getError(new UserException("Failed to find cart - Wrong ID")), result);
    }

    @Test
    void testRemoveProductFromCartSuccess() throws AuthenticationFailedException, NoPermissionException, ShoppingCartException, UserException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        CartRequest cartRequest = new CartRequest(storeName, productId, 1);
        Request request = new Request(userId,"token",JsonUtils.serialize(cartRequest));
        doNothing().when(usersController).removeProductFromCart(userId,storeName , productId,request.token());
        String result = userProfilesService.removeProductFromCart(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void testRemoveProductFromCartFailureWrongIdOfUser() throws AuthenticationFailedException, NoPermissionException, ShoppingCartException, UserException {

        String userId = "wrongId";
        String storeName = "storeName";
        String productId = "productId";
        CartRequest cartRequest = new CartRequest(storeName, productId, 1);
        Request request = new Request(userId,"token",JsonUtils.serialize(cartRequest));
        doThrow(new UserException("Failed to remove product from cart - Wrong user id")).when(usersController).removeProductFromCart(userId, storeName, productId,request.token());
        String result = userProfilesService.removeProductFromCart(request.toJson());
        assertEquals(Response.getError(new UserException("Failed to remove product from cart - Wrong user id")), result);
    }

    @Test
    void testRemoveProductFromCartFailureWrongStoreName() throws AuthenticationFailedException, NoPermissionException, ShoppingCartException, UserException {

        String userId = "userId";
        String storeName = "wrongStoreName";
        String productId = "productId";
        CartRequest cartRequest = new CartRequest(storeName, productId, 1);
        Request request = new Request(userId,"token",JsonUtils.serialize(cartRequest));
        doThrow(new ShoppingCartException("Failed to remove product from cart - Wrong store name")).when(usersController).removeProductFromCart(userId, storeName, productId,request.token());
        String result = userProfilesService.removeProductFromCart(request.toJson());
        assertEquals(Response.getError(new ShoppingCartException("Failed to remove product from cart - Wrong store name")), result);
    }

    @Test
    void testRemoveProductFromCartFailureWrongProductId() throws AuthenticationFailedException, NoPermissionException, ShoppingCartException, UserException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "wrongProductId";
        CartRequest cartRequest = new CartRequest(storeName, productId, 1);
        Request request = new Request(userId,"token",JsonUtils.serialize(cartRequest));
        doThrow(new ShoppingCartException("Failed to remove product from cart - Wrong product id")).when(usersController).removeProductFromCart(userId, storeName, productId,request.token());
        String result = userProfilesService.removeProductFromCart(request.toJson());
        assertEquals(Response.getError(new ShoppingCartException("Failed to remove product from cart - Wrong product id")), result);
    }

    @Test
    void testChangeProductQuantitySuccess() throws AuthenticationFailedException, NoPermissionException, ShoppingCartException, UserException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        CartRequest cartRequest = new CartRequest(storeName, productId, 1);
        Request request = new Request(userId,"token",JsonUtils.serialize(cartRequest));
        doNothing().when(usersController).changeProductQuantity(userId, storeName, productId, 1,request.token());
        String result = userProfilesService.changeProductQuantity(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void testChangeProductQuantityFailureNegativeQuantity() throws AuthenticationFailedException, NoPermissionException, ShoppingCartException, UserException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        CartRequest cartRequest = new CartRequest(storeName, productId, -2);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        doThrow(new ShoppingCartException("Failed to change product quantity - Negative quantity")).when(usersController).changeProductQuantity(userId, storeName, productId, -2,request.token());
        String result = userProfilesService.changeProductQuantity(request.toJson());
        assertEquals(Response.getError(new ShoppingCartException("Failed to change product quantity - Negative quantity")), result);
    }

    @Test
    void testChangeProductQuantityFailureQuantityTooHigh() throws AuthenticationFailedException, NoPermissionException, ShoppingCartException, UserException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        int quantity = 20;
        CartRequest cartRequest = new CartRequest(storeName, productId, quantity);
        Request request = new Request(userId,"token",JsonUtils.serialize(cartRequest));
        doThrow(new ShoppingCartException("Failed to change product quantity - quantity too high")).when(usersController).changeProductQuantity(userId, storeName, productId, quantity,request.token());
        String result = userProfilesService.changeProductQuantity(request.toJson());
        assertEquals(Response.getError(new ShoppingCartException("Failed to change product quantity - quantity too high")), result);
    }

//---------------------------UseCase 2.2.5-------------------------
    @Test
    void testStartPurchaseSuccess() throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException, UserException {

        String userId = "userId";
        Request request = new Request(userId,"token","");
        doNothing().when(usersController).startPurchase(userId,request.token());
        String result = userProfilesService.startPurchase(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void testStartPurchaseFailureWrongId() throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException, UserException {

        String userId = "wrongId";
        Request request = new Request(userId,"token","");
        doThrow(new UserException("Failed to start purchase - Wrong ID")).when(usersController).startPurchase(userId,request.token());
        String result = userProfilesService.startPurchase(request.toJson());
        assertEquals(Response.getError(new UserException("Failed to start purchase - Wrong ID")), result);
    }

    @Test
    void testStartPurchaseFailureEmptyCart() throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException, UserException {
        String userId = "userId";
        Request request = new Request(userId,"token","");
        doThrow(new PurchaseFailedException("Failed to start purchase - cart is empty")).when(usersController).startPurchase(userId,request.token());
        String result = userProfilesService.startPurchase(request.toJson());
        assertEquals(Response.getError(new PurchaseFailedException("Failed to start purchase - cart is empty")), result);
    }

    @Test
    void testStartPurchaseFailureSomeProductsReservationFail() throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException, UserException {
        String userId = "userId";
        Request request = new Request(userId,"token","");
        doThrow(new PurchaseFailedException("Failed to start purchase - Could not reserve some of the products in the cart.")).when(usersController).startPurchase(userId,request.token());
        String result = userProfilesService.startPurchase(request.toJson());
        assertEquals(Response.getError(new PurchaseFailedException("Failed to start purchase - Could not reserve some of the products in the cart.")), result);
    }

    @Test
    void testPayForPurchaseSuccess() throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException, UserException {

        String userId = "userId";
        Request request = new Request(userId,"token","");
        doNothing().when(usersController).payForPurchase(userId,request.token());
        String result = userProfilesService.payForPurchase(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void testPayForPurchaseFailureWrongId() throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException, UserException {

        String userId = "wrongId";
        Request request = new Request(userId,"token","");
        doThrow(new UserException("Failed to pay for purchase - Wrong ID")).when(usersController).payForPurchase(userId,request.token());
        String result = userProfilesService.payForPurchase(request.toJson());
        assertEquals(Response.getError(new UserException("Failed to pay for purchase - Wrong ID")), result);
    }
    @Test
    void testPayForPurchaseFailurePaymentFail() throws PurchaseFailedException, NoPermissionException, AuthenticationFailedException, UserException {

        String userId = "userId";
        Request request = new Request(userId,"token","");
        doThrow(new PurchaseFailedException("Failed to complete the purchase - Payment failed")).when(usersController).payForPurchase(userId,request.token());
        String result = userProfilesService.payForPurchase(request.toJson());
        assertEquals(Response.getError(new PurchaseFailedException("Failed to complete the purchase - Payment failed")), result);
    }

    @Test
    void testCancelPurchaseSuccess() throws NoPermissionException, AuthenticationFailedException, UserException {

        String userId = "userId";
        Request request = new Request(userId,"token","");
        doNothing().when(usersController).cancelPurchase(userId,request.token());
        String result = userProfilesService.cancelPurchase(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void testCancelPurchaseFailureWrongId() throws NoPermissionException, AuthenticationFailedException, UserException {

        String userId = "wrongId";
        Request request = new Request(userId,"token","");
        doThrow(new UserException("Failed to cancel purchase - Wrong ID")).when(usersController).cancelPurchase(userId,request.token());
        String result = userProfilesService.cancelPurchase(request.toJson());
        assertEquals(Response.getError(new UserException("Failed to cancel purchase - Wrong ID")), result);
    }

// -------------------------UseCase 2.3.1-------------------------
    @Test
    void testLogoutSuccess() throws AuthenticationFailedException, UserException {

        String userId = "userId";
        Request request = new Request(userId,"token","");
        String result = userProfilesService.logout(request.toJson());
        assertEquals(Response.getOk(), result);
    }

    @Test
    void testLogoutFailureWrongId() throws AuthenticationFailedException, UserException {

        String userId = "wrongId";
        Request request = new Request(userId,"token","");
        doThrow(new UserException("Failed to logout - Wrong ID")).when(usersController).logout(userId,request.token());
        String result = userProfilesService.logout(request.toJson());
        assertEquals(Response.getError(new UserException("Failed to logout - Wrong ID")), result);
    }

}
