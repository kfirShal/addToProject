package com.amazonas.acceptanceTests;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.permissions.proxies.UserProxy;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.UsersController;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.exceptions.UserException;
import com.amazonas.service.UserProfilesService;
import com.amazonas.service.requests.Request;
import com.amazonas.service.requests.users.CartRequest;
import com.amazonas.service.requests.users.LoginRequest;
import com.amazonas.service.requests.users.RegisterRequest;
import com.amazonas.utils.JsonUtils;
import com.amazonas.utils.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UsersAcceptanceTests {
    @Mock
    private UserProxy usersController;
    private Product product;
    private ShoppingCart shoppingCart;
    @InjectMocks
    private UserProfilesService userProfilesService;
    @BeforeEach
    void setUp() {
        // Manually create a mock for UsersController
        usersController = mock(UserProxy.class);
        product = mock(Product.class);
        shoppingCart = mock(ShoppingCart.class);
        userProfilesService = new UserProfilesService(usersController);
    }


//-------------------------UseCase 2.1.1-------------------------
    @Test
    void testEnterAsGuestSuccess() {

        when(usersController.enterAsGuest()).thenReturn("Entered as guest successfully");
        String result = userProfilesService.enterAsGuest();
        assertEquals(new Response("Entered as guest successfully", true).toJson(), result);
    }

    @Test
    void testEnterAsGuestFailure() {

        doThrow(new RuntimeException("Failed to enter as guest")).when(usersController).enterAsGuest();
        String result = userProfilesService.enterAsGuest();
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to enter as guest")).toJson(), result);
    }


//-------------------------UseCase 2.1.2-------------------------
    @Test
    void logoutAsGuestSuccess() throws AuthenticationFailedException {
        doNothing().when(usersController).logoutAsGuest("guestId","");
        Request request = new Request("guestId","token","");
        String result = userProfilesService.logoutAsGuest(request.toJson());
        assertEquals(new Response("Logged out as guest successfully", true).toJson(), result);
    }

    @Test
    void logoutAsGuestFailure() throws AuthenticationFailedException {

        doThrow(new RuntimeException("Failed to logout as guest")).when(usersController).logoutAsGuest("guestId","");
        Request request = new Request("guestId","token","");
        String result = userProfilesService.logoutAsGuest(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to logout as guest")).toJson(), result);
    }


//-------------------------UseCase 2.1.3-------------------------
    @Test
    void testRegisterSuccess() throws UserException {

        String email = "test@example.com";
        String userName = "testUser";
        String password = "password123";
        doNothing().when(usersController).register(email, userName, password);
        RegisterRequest registerRequest = new RegisterRequest(email, userName, password);
        Request request = new Request("userId","token",JsonUtils.serialize(registerRequest));
        String result = userProfilesService.register(request.toJson());
        assertEquals(new Response("User registered successfully", true).toJson(), result);
    }

    @Test
    void testRegisterFailureInvalidPassword() throws UserException {

        String email = "test@example.com";
        String userName = "testUser";
        String password = "password";
        doThrow(new RuntimeException("Registration failed - Illegal Password")).when(usersController).register(email, userName, password);
        RegisterRequest registerRequest = new RegisterRequest(email, userName, password);
        Request request = new Request("userId","token",JsonUtils.serialize(registerRequest));
        String result = userProfilesService.register(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Registration failed - Illegal Password")).toJson(), result);
    }
    @Test
    void testRegisterFailureUsernameAlreadyExists() throws UserException {

        String email = "test@example.com";
        String userName = "existsTestUser";
        String password = "password";
        doThrow(new RuntimeException("Registration failed - Username Already Exists")).when(usersController).register(email, userName, password);
        RegisterRequest registerRequest = new RegisterRequest(email, userName, password);
        Request request = new Request("userId","token",JsonUtils.serialize(registerRequest));
        String result = userProfilesService.register(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Registration failed - Username Already Exists")).toJson(), result);
    }
//-------------------------UseCase 2.1.4-------------------------
    @Test
    void testLoginToRegisteredSuccess() throws AuthenticationFailedException {

        String guestId = "guestId";
        String userName = "testUser";
        doNothing().when(usersController).loginToRegistered(guestId, userName,"");
        LoginRequest loginRequest = new LoginRequest(guestId, userName);
        Request request = new Request("userId","token",JsonUtils.serialize(loginRequest));
        String result = userProfilesService.loginToRegistered(request.toJson());
        assertEquals(new Response("Logged in successfully", true).toJson(), result);
    }

    @Test
    void testLoginToRegisteredFailureWrongUsername() throws AuthenticationFailedException {

        String guestId = "guestId";
        String userName = "testUser43"; // instead of "testUser" -- the correct username
        doThrow(new RuntimeException("Failed to login - Wrong username")).when(usersController).loginToRegistered(guestId, userName,"");
        LoginRequest loginRequest = new LoginRequest(guestId, userName);
        Request request = new Request("userId","token",JsonUtils.serialize(loginRequest));
        String result = userProfilesService.loginToRegistered(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to login - Wrong username")).toJson(), result);
    }


//---------------------------UseCase 2.2.3-------------------------
    @Test
    void addProductToCartSuccess() throws AuthenticationFailedException, NoPermissionException {

        String userId = "userId";
        String storeName = "storeName";
        doNothing().when(usersController).addProductToCart(userId, storeName, product.productId(), 1,"token");
        CartRequest cartRequest = new CartRequest(storeName, product.productId(), 1);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        String result = userProfilesService.addProductToCart(request.toJson());
        assertEquals(new Response("Product added to cart successfully", true).toJson(), result);
    }

    @Test
    void addProductToCartFailureProductOutOfStock() throws AuthenticationFailedException, NoPermissionException {

        String userId = "userId";
        String storeName = "storeName";
        doThrow(new RuntimeException("Failed to add product to cart - the product is out of stock")).when(usersController).addProductToCart(userId, storeName, product.productId(), 1,"token");
        CartRequest cartRequest = new CartRequest(storeName, product.productId(), 1);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        String result = userProfilesService.addProductToCart(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to add product to cart - the product is out of stock")).toJson(), result);
    }
//---------------------------UseCase 2.2.4-------------------------
    @Test
    void testViewCartSuccess() throws AuthenticationFailedException, NoPermissionException {

        String userId = "userId";
        when((usersController).getCart(userId,"")).thenReturn(shoppingCart);
        String result = userProfilesService.ViewCart(userId);
        assertEquals(new Response("Cart found successfully", true,shoppingCart).toJson(), result);
    }

    @Test
    void testViewCartFailureWrongId() throws AuthenticationFailedException, NoPermissionException {

        String userId = "wrongId";
        doThrow(new RuntimeException("Failed to find cart - Wrong ID")).when(usersController).getCart(userId,"token");
        Request request = new Request("userId","token","");
        String result = userProfilesService.ViewCart(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to find cart - Wrong ID")).toJson(), result);
    }

    @Test
    void testRemoveProductFromCartSuccess() throws AuthenticationFailedException, NoPermissionException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        doNothing().when(usersController).RemoveProductFromCart(userId, storeName, productId,"token");
        CartRequest cartRequest = new CartRequest(storeName, productId, 0);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        String result = userProfilesService.RemoveProductFromCart(request.toJson());
        assertEquals(new Response("Product removed from cart successfully", true).toJson(), result);
    }

    @Test
    void testRemoveProductFromCartFailureWrongIdOfUser() throws AuthenticationFailedException, NoPermissionException {

        String userId = "wrongId";
        String storeName = "storeName";
        String productId = "productId";
        doThrow(new RuntimeException("Failed to remove product from cart - Wrong user id")).when(usersController).RemoveProductFromCart(userId, storeName, productId,"token");
        CartRequest cartRequest = new CartRequest(storeName, productId, 0);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        String result = userProfilesService.RemoveProductFromCart(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to remove product from cart - Wrong user id")).toJson(), result);
    }

    @Test
    void testRemoveProductFromCartFailureWrongStoreName() throws AuthenticationFailedException, NoPermissionException {

        String userId = "userId";
        String storeName = "wrongStoreName";
        String productId = "productId";
        doThrow(new RuntimeException("Failed to remove product from cart - Wrong store name")).when(usersController).RemoveProductFromCart(userId, storeName, productId,"token");
        CartRequest cartRequest = new CartRequest(storeName, productId, 0);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        String result = userProfilesService.RemoveProductFromCart(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to remove product from cart - Wrong store name")).toJson(), result);
    }

    @Test
    void testRemoveProductFromCartFailureWrongProductId() throws AuthenticationFailedException, NoPermissionException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "wrongProductId";
        doThrow(new RuntimeException("Failed to remove product from cart - Wrong product id")).when(usersController).RemoveProductFromCart(userId, storeName, productId,"token");
        CartRequest cartRequest = new CartRequest(storeName, productId, 0);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        String result = userProfilesService.RemoveProductFromCart(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to remove product from cart - Wrong product id")).toJson(), result);
    }

    @Test
    void testChangeProductQuantitySuccess() throws AuthenticationFailedException, NoPermissionException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        int quantity = 1;
        doNothing().when(usersController).changeProductQuantity(userId, storeName, productId, quantity,"token");
        CartRequest cartRequest = new CartRequest(storeName, productId, quantity);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        String result = userProfilesService.changeProductQuantity(request.toJson());
        assertEquals(new Response("Product quantity changed successfully", true).toJson(), result);
    }

    @Test
    void testChangeProductQuantityFailureNegativeQuantity() throws AuthenticationFailedException, NoPermissionException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        int quantity = -1;
        doThrow(new RuntimeException("Failed to change product quantity - Negative quantity")).when(usersController).changeProductQuantity(userId, storeName, productId, quantity,"token");
        CartRequest cartRequest = new CartRequest(storeName, productId, quantity);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        String result = userProfilesService.changeProductQuantity(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to change product quantity - Negative quantity")).toJson(), result);
    }

    @Test
    void testChangeProductQuantityFailureQuantityTooHigh() throws AuthenticationFailedException, NoPermissionException {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        int quantity = 20;
        doThrow(new RuntimeException("Failed to change product quantity - quantity too high")).when(usersController).changeProductQuantity(userId, storeName, productId, quantity,"token");
        CartRequest cartRequest = new CartRequest(storeName, productId, quantity);
        Request request = new Request("userId","token",JsonUtils.serialize(cartRequest));
        String result = userProfilesService.changeProductQuantity(request.toJson());
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to change product quantity - quantity too high")).toJson(), result);
    }


// -------------------------UseCase 2.3.1-------------------------
    @Test
    void testLogoutSuccess() throws AuthenticationFailedException {

        String userId = "userId";
        doNothing().when(usersController).logout(userId,"");
        String result = userProfilesService.logout(userId);
        assertEquals(new Response("Logged out successfully", true).toJson(), result);
    }

    @Test
    void testLogoutFailureWrongId() throws AuthenticationFailedException {

        String userId = "wrongId";
        doThrow(new RuntimeException("Failed to logout - Wrong ID")).when(usersController).logout(userId,"");
        String result = userProfilesService.logout(userId);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to logout - Wrong ID")).toJson(), result);
    }

}
