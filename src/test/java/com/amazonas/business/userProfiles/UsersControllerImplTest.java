package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UsersControllerImplTest {

    private UsersControllerImpl usersController;
    @BeforeEach
    void setUp() {
         usersController = new UsersControllerImpl();
         usersController.register("testEmail", "testUserName", "testPassword@");



    }

    @AfterEach
    void tearDown() {
        usersController = null;
    }

    @Test
    void registerSuccess() {
        usersController.register("testEmail1", "testUserName1", "testPassword1@");
        assertNotNull(usersController.getRegisteredUser("testUserName1"));
    }
    @Test
    void registerFailure() {

        assertThrows(RuntimeException.class, () -> usersController.register("testEmail", "testUserName", "testPassword@"));
    }


    @Test
    void enterAsGuestSuccess() {
        String guestId = usersController.enterAsGuest();
        assertNotNull(usersController.getGuest(guestId));

    }
    @Test
    void loginToRegisteredSuccess() {
        String guestId = usersController.enterAsGuest();
        usersController.register("testEmail1", "testUserName1", "testPassword1@");
        usersController.loginToRegistered(guestId, "testUserName1");
        assertNotNull(usersController.getOnlineUser("testUserName1"));
    }
    @Test
    void loginToRegisteredFailure() {
        String guestId = usersController.enterAsGuest();
        assertThrows(RuntimeException.class, () -> usersController.loginToRegistered(guestId, "testUserName1"));
        assertNull(usersController.getOnlineUser(guestId));
    }

    @Test
    void logoutSuccess() {
        String guestId = usersController.enterAsGuest();
        usersController.loginToRegistered(guestId, "testUserName");
        usersController.logout("testUserName");
        assertNull(usersController.getOnlineUser(guestId));
    }
    @Test
    void logoutFailure() {
        String guestId = usersController.enterAsGuest();
        assertThrows(RuntimeException.class, () -> usersController.logout(guestId));
    }

    @Test
    void logoutAsGuestSuccess() {
        String guestId = usersController.enterAsGuest();
        usersController.logoutAsGuest(guestId);
        assertNull(usersController.getGuest(guestId));
    }
    @Test
    void logoutAsGuestFailure() {
        assertThrows(RuntimeException.class, () -> usersController.logoutAsGuest("wrongId"));
    }

    @Test
    void getCartSuccess() {
        ShoppingCart cart= usersController.getCart("testUserName");
        assertEquals(cart, usersController.getCart("testUserName"));

    }
    @Test
    void getCartFailure() {
        assertThrows(RuntimeException.class, () -> usersController.getCart("wrongId"));
    }

    @Test
    void addProductToCartSuccess() {
        usersController.addProductToCart("testUserName", "testStoreName", new Product("productId","name",100,"category",5,""), 1);
        assertEquals(1, usersController.getCart("testUserName").getBasket("testStoreName").getProducts().size());
    }
    @Test
    void addProductToCartFailure() {
        assertThrows(RuntimeException.class, () -> usersController.addProductToCart("testUserName1", "testStoreName", new Product("productId","name",100,"category",5,""), 1));
    }

    @Test
    void removeProductFromCartSuccess() {
        usersController.addProductToCart("testUserName", "testStoreName", new Product("productId","name",100,"category",5,""), 1);
        assertEquals(1, usersController.getCart("testUserName").getBasket("testStoreName").getProducts().size());
        usersController.RemoveProductFromCart("testUserName", "testStoreName", "productId");
        assertEquals(0, usersController.getCart("testUserName").getBasket("testStoreName").getProducts().size());

    }
    @Test
    void removeProductFromCartFailure() {
        assertThrows(RuntimeException.class, () -> usersController.RemoveProductFromCart("testUserName", "testStoreName", "productId"));
    }

    @Test
    void changeProductQuantitySuccess() {
        usersController.addProductToCart("testUserName", "testStoreName", new Product("productId","name",100,"category",5,""), 1);
        assertEquals(1, usersController.getCart("testUserName").getBasket("testStoreName").getProducts().size());
        usersController.changeProductQuantity("testUserName", "testStoreName", "productId", 5);
        assertEquals(5, usersController.getCart("testUserName").getBasket("testStoreName").getProducts().get("productId").getSecond());
    }
    @Test
    void changeProductQuantityFailure() {
        assertThrows(RuntimeException.class, () -> usersController.changeProductQuantity("testUserName", "testStoreName", "productId", 5));
    }
}