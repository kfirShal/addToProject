package com.amazonas.acceptanceTests;


import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoresController;
import com.amazonas.business.stores.factories.StoreCallbackFactory;
import com.amazonas.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.business.stores.reservations.ReservationFactory;
import com.amazonas.business.stores.storePositions.AppointmentSystem;
import com.amazonas.business.userProfiles.*;
import com.amazonas.exceptions.ShoppingCartException;
import com.amazonas.exceptions.UserException;
import com.amazonas.repository.*;
import com.amazonas.repository.mongoCollections.ProductMongoCollection;
import com.amazonas.repository.mongoCollections.ShoppingCartMongoCollection;
import com.amazonas.repository.mongoCollections.TransactionMongoCollection;
import com.amazonas.repository.mongoCollections.UserMongoCollection;
import com.amazonas.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VisitorActionsRequirements {

    private UsersController usersController;
    private ShoppingCartRepository shoppingCartRepository;
    private ShoppingCartFactory shoppingCartFactory;
    private Guest guest;
    private ShoppingCartMongoCollection shoppingCartMongo;
    private StoreBasketFactory storeBasketFactory;
    private UserMongoCollection userMongo;
    private TransactionMongoCollection transMongo;
    private ProductMongoCollection productMongo;
    private UserCredentialsRepository userCredentialsRepository;
    private Rating rating;
    private ProductInventory productInventory;
    private AppointmentSystem appointmentSystem;
    private ReservationFactory reservationFactory;
    private PendingReservationMonitor pendingReservationMonitor;
    private PermissionsController permissionsController;
    private TransactionRepository transactionRepository;
    private StoreCallbackFactory storeCallbackFactory;
    private StoresController storesController;
    private UserRepository userRepository;
    private AuthenticationController authenticationController;

    @BeforeEach
    public void setup() {
        shoppingCartRepository = new ShoppingCartRepository(shoppingCartMongo);
        shoppingCartFactory = new ShoppingCartFactory(storeBasketFactory);
        storeBasketFactory = new StoreBasketFactory(storeCallbackFactory);
        storeCallbackFactory = new StoreCallbackFactory(storesController);
        userRepository = new UserRepository(userMongo);
        authenticationController = new AuthenticationController(userCredentialsRepository);

        usersController = new UsersController(
                userRepository,
                new ReservationRepository(),
                new TransactionRepository(transMongo),
                new ProductRepository(productMongo),
                new PaymentService(),
                shoppingCartFactory,
                authenticationController,
                shoppingCartRepository
        );
    }

    //-------------------------Guest entry-------------------------

    @Test
    public void testGuestUserCanSearchForProducts() throws ShoppingCartException, UserException {
        // Arrange
        String guestId = usersController.enterAsGuest();
        Product product = new Product("product1", "LAPTOP", 100.0, "Technologies", "PC", rating.FIVE_STARS);
        Store store = new Store("store1",
                "KSP",
                "STORE",
                rating.FIVE_STARS,
                productInventory ,
                appointmentSystem,
                reservationFactory,
                pendingReservationMonitor,
                permissionsController,
                transactionRepository);
        // Act
        usersController.addProductToCart(guestId, "store1", "product1", 1);
        ShoppingCart shoppingCart = new ShoppingCart(storeBasketFactory, guestId);

        // Assert
        assertTrue(shoppingCart.getTotalPrice() != 0);
    }

    //-------------------------Guest exit-------------------------

    @Test
    public void testGuestIndicatesIntentionToLeave() throws UserException {
        // Arrange
        String guestId = usersController.enterAsGuest();

        // Act
        usersController.logoutAsGuest(guestId);

        // Assert
        assertFalse(usersController.getGuests().containsKey(guestId));
    }

    @Test
    public void testGuestDoesNotIndicateIntentionToLeave() throws UserException {
        // Arrange
        String guestId = usersController.enterAsGuest();
        ShoppingCart guestCart = shoppingCartRepository.getCart(guestId);

        // Act
        // Guest does not explicitly log out, hence no action is taken

        // Assert
        assertTrue(usersController.getGuests().containsKey(guestId));
    }

    //-------------------------Registration-------------------------

    @Test
    public void testSuccessfulRegistration() throws UserException {
        // Arrange
        String email = "uniqueemail@example.com";
        String userId = "uniqueUser123";
        String password = "ValidPassword1!";

        // Act & Assert
        try {
            usersController.register(email, userId, password);
        } catch (UserException e) {
            throw new RuntimeException(e);
        }

        assertTrue(userRepository.userIdExists(userId), "Registered user ID should exist in the user repository");
    }

    @Test
    public void testRegistrationWithTakenUsername() throws UserException {
        // Arrange
        String email = "uniqueemail@example.com";
        String userId = "existingUser123";
        String password = "ValidPassword1!";

        // Pre-register a user with the same userId
        usersController.register("anotheremail@example.com", userId, "AnotherValidPassword1!");

        // Act & Assert
        assertThrows(UserException.class, () -> {
            usersController.register(email, userId, password);
        }, "Registration with taken username should throw a UserException");
    }

    @Test
    public void testRegistrationWithIncompleteDetails() {
        // Arrange
        String email = "uniqueemail@example.com";
        String userId = "uniqueUser123";
        String password = "";  // Incomplete detail

        // Act & Assert
        assertThrows(UserException.class, () -> {
            usersController.register(email, userId, password);
        }, "Registration with incomplete details should throw a UserException");
    }

    //-------------------------Login-------------------------

    @Test
    public void testSuccessfulLogin() throws UserException {
        // Arrange
        String userId = "validUser";
        String password = "ValidPassword1!";

        // Act
        String guestId = usersController.enterAsGuest();
        ShoppingCart shoppingCart = usersController.loginToRegistered(guestId, userId);

        // Assert
        assertNotNull(shoppingCart, "User should have a shopping cart after successful login");
        assertTrue(usersController.getOnlineRegisteredUsers().containsKey(userId), "User should be recognized as logged in");
    }

    @Test
    public void testInvalidLogin() {
        // Arrange
        String userId = "invalidUser";
        String password = "InvalidPassword";

        // Act & Assert
        assertThrows(UserException.class, () -> {
            String guestId = usersController.enterAsGuest();
            usersController.loginToRegistered(guestId, userId);
        }, "System should throw an exception for invalid login credentials");
    }
}







