package com.amazonas.acceptanceTests;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.UsersController;
import com.amazonas.service.UserProfilesService;
import com.amazonas.utils.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class UsersAcceptanceTests {
    @Mock
    private UsersController usersController;
    private Product product;
    private ShoppingCart shoppingCart;
    @InjectMocks
    private UserProfilesService userProfilesService;
    @BeforeEach
    void setUp() {
        // Manually create a mock for UsersController
        usersController = mock(UsersController.class);
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
    void logoutAsGuestSuccess() {

        doNothing().when(usersController).logoutAsGuest("guestId");
        String result = userProfilesService.logoutAsGuest("guestId");
        assertEquals(new Response("Logged out as guest successfully", true).toJson(), result);
    }

    @Test
    void logoutAsGuestFailure() {

        doThrow(new RuntimeException("Failed to logout as guest")).when(usersController).logoutAsGuest("guestId");
        String result = userProfilesService.logoutAsGuest("guestId");
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to logout as guest")).toJson(), result);
    }


//-------------------------UseCase 2.1.3-------------------------
    @Test
    void testRegisterSuccess() {

        String email = "test@example.com";
        String userName = "testUser";
        String password = "password123";
        doNothing().when(usersController).register(email, userName, password);
        String result = userProfilesService.register(email, userName, password);
        assertEquals(new Response("User registered successfully", true).toJson(), result);
    }

    @Test
    void testRegisterFailureInvalidPassword() {

        String email = "test@example.com";
        String userName = "testUser";
        String password = "password";
        doThrow(new RuntimeException("Registration failed - Illegal Password")).when(usersController).register(email, userName, password);
        String result = userProfilesService.register(email, userName, password);
        assertEquals(Response.getErrorResponse(new RuntimeException("Registration failed - Illegal Password")).toJson(), result);
    }
    @Test
    void testRegisterFailureUsernameAlreadyExists() {

        String email = "test@example.com";
        String userName = "existsTestUser";
        String password = "password";
        doThrow(new RuntimeException("Registration failed - Username Already Exists")).when(usersController).register(email, userName, password);
        String result = userProfilesService.register(email, userName, password);
        assertEquals(Response.getErrorResponse(new RuntimeException("Registration failed - Username Already Exists")).toJson(), result);
    }
//-------------------------UseCase 2.1.4-------------------------
    @Test
    void testLoginToRegisteredSuccess() {

        String guestId = "guestId";
        String userName = "testUser";
        doNothing().when(usersController).loginToRegistered(guestId, userName);
        String result = userProfilesService.loginToRegistered(guestId, userName);
        assertEquals(new Response("Logged in successfully", true).toJson(), result);
    }

    @Test
    void testLoginToRegisteredFailureWrongUsername() {

        String guestId = "guestId";
        String userName = "testUser43"; // instead of "testUser" -- the correct username
        doThrow(new RuntimeException("Failed to login - Wrong username")).when(usersController).loginToRegistered(guestId, userName);
        String result = userProfilesService.loginToRegistered(guestId, userName);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to login - Wrong username")).toJson(), result);
    }


//---------------------------UseCase 2.2.3-------------------------
    @Test
    void addProductToCartSuccess() {

        String userId = "userId";
        String storeName = "storeName";
        doNothing().when(usersController).addProductToCart(userId, storeName, product, 1);
        String result = userProfilesService.addProductToCart(userId, storeName, product, 1);
        assertEquals(new Response("Product added to cart successfully", true).toJson(), result);
    }

    @Test
    void addProductToCartFailureProductOutOfStock() {

        String userId = "userId";
        String storeName = "storeName";
        doThrow(new RuntimeException("Failed to add product to cart - the product is out of stock")).when(usersController).addProductToCart(userId, storeName, product, 1);
        String result = userProfilesService.addProductToCart(userId, storeName, product, 1);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to add product to cart - the product is out of stock")).toJson(), result);
    }
//---------------------------UseCase 2.2.4-------------------------
    @Test
    void testViewCartSuccess() {

        String userId = "userId";
        when((usersController).getCart(userId)).thenReturn(shoppingCart);
        String result = userProfilesService.ViewCart(userId);
        assertEquals(new Response("Cart found successfully", true,shoppingCart).toJson(), result);
    }

    @Test
    void testViewCartFailureWrongId() {

        String userId = "wrongId";
        doThrow(new RuntimeException("Failed to find cart - Wrong ID")).when(usersController).getCart(userId);
        String result = userProfilesService.ViewCart(userId);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to find cart - Wrong ID")).toJson(), result);
    }

    @Test
    void testRemoveProductFromCartSuccess() {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        doNothing().when(usersController).RemoveProductFromCart(userId, storeName, productId);
        String result = userProfilesService.RemoveProductFromCart(userId, storeName, productId);
        assertEquals(new Response("Product removed from cart successfully", true).toJson(), result);
    }

    @Test
    void testRemoveProductFromCartFailureWrongIdOfUser() {

        String userId = "wrongId";
        String storeName = "storeName";
        String productId = "productId";
        doThrow(new RuntimeException("Failed to remove product from cart - Wrong user id")).when(usersController).RemoveProductFromCart(userId, storeName, productId);
        String result = userProfilesService.RemoveProductFromCart(userId, storeName, productId);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to remove product from cart - Wrong user id")).toJson(), result);
    }

    @Test
    void testRemoveProductFromCartFailureWrongStoreName() {

        String userId = "userId";
        String storeName = "wrongStoreName";
        String productId = "productId";
        doThrow(new RuntimeException("Failed to remove product from cart - Wrong store name")).when(usersController).RemoveProductFromCart(userId, storeName, productId);
        String result = userProfilesService.RemoveProductFromCart(userId, storeName, productId);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to remove product from cart - Wrong store name")).toJson(), result);
    }

    @Test
    void testRemoveProductFromCartFailureWrongProductId() {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "wrongProductId";
        doThrow(new RuntimeException("Failed to remove product from cart - Wrong product id")).when(usersController).RemoveProductFromCart(userId, storeName, productId);
        String result = userProfilesService.RemoveProductFromCart(userId, storeName, productId);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to remove product from cart - Wrong product id")).toJson(), result);
    }

    @Test
    void testChangeProductQuantitySuccess() {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        int quantity = 1;
        doNothing().when(usersController).changeProductQuantity(userId, storeName, productId, quantity);
        String result = userProfilesService.changeProductQuantity(userId, storeName, productId, quantity);
        assertEquals(new Response("Product quantity changed successfully", true).toJson(), result);
    }

    @Test
    void testChangeProductQuantityFailureNegativeQuantity() {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        int quantity = -1;
        doThrow(new RuntimeException("Failed to change product quantity - Negative quantity")).when(usersController).changeProductQuantity(userId, storeName, productId, quantity);
        String result = userProfilesService.changeProductQuantity(userId, storeName, productId, quantity);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to change product quantity - Negative quantity")).toJson(), result);
    }

    @Test
    void testChangeProductQuantityFailureQuantityTooHigh() {

        String userId = "userId";
        String storeName = "storeName";
        String productId = "productId";
        int quantity = 20;
        doThrow(new RuntimeException("Failed to change product quantity - quantity too high")).when(usersController).changeProductQuantity(userId, storeName, productId, quantity);
        String result = userProfilesService.changeProductQuantity(userId, storeName, productId, quantity);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to change product quantity - quantity too high")).toJson(), result);
    }


// -------------------------UseCase 2.3.1-------------------------
    @Test
    void testLogoutSuccess() {

        String userId = "userId";
        doNothing().when(usersController).logout(userId);
        String result = userProfilesService.logout(userId);
        assertEquals(new Response("Logged out successfully", true).toJson(), result);
    }

    @Test
    void testLogoutFailureWrongId() {

        String userId = "wrongId";
        doThrow(new RuntimeException("Failed to logout - Wrong ID")).when(usersController).logout(userId);
        String result = userProfilesService.logout(userId);
        assertEquals(Response.getErrorResponse(new RuntimeException("Failed to logout - Wrong ID")).toJson(), result);
    }

}
