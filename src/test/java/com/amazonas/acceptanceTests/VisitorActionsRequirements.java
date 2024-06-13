package com.amazonas.acceptanceTests;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

    @BeforeEach
    public void setup() {
        shoppingCartRepository = new ShoppingCartRepository(shoppingCartMongo);
        shoppingCartFactory = new ShoppingCartFactory(storeBasketFactory);
        usersController = new UsersController(
                new UserRepository(userMongo),
                new ReservationRepository(),
                new TransactionRepository(transMongo),
                new ProductRepository(productMongo),
                new PaymentService(),
                shoppingCartFactory,
                new AuthenticationController(userCredentialsRepository),
                shoppingCartRepository
        );
        storeBasketFactory = new StoreBasketFactory(storeCallbackFactory);
        storeCallbackFactory = new StoreCallbackFactory(storesController);
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


    @Test
    public void testInvalidLoginFallsBackToGuest() throws UserException, ShoppingCartException {
        // Arrange
        String invalidUserId = "invalidUser";
        String invalidPassword = "wrongPassword";

        boolean loginFailed = false;
        try {
            usersController.loginToRegistered(invalidUserId, invalidPassword);
        } catch (UserException e) {
            loginFailed = true; // Expected exception for invalid login
        }

        // User continues as a guest
        String guestId = usersController.enterAsGuest();
        Guest guest = usersController.getGuest(guestId);

        // Act
        Product product = new Product("product1", "LAPTOP", 100.0, "Technologies", "PC", rating.FIVE_STARS);
        Store store = new Store("store1",
                "KSP",
                "STORE",
                rating.FIVE_STARS,
                new ProductInventory(), // assuming a valid product inventory
                new AppointmentSystem(), // assuming a valid appointment system
                new ReservationFactory(), // assuming a valid reservation factory
                new PendingReservationMonitor(), // assuming a valid pending reservation monitor
                new PermissionsController(), // assuming a valid permissions controller
                new TransactionRepository()); // assuming a valid transaction repository

        usersController.addProductToCart(guestId, "store1", "product1", 1);
        ShoppingCart shoppingCart = shoppingCartRepository.getCart(guestId);

        // Assert
        assertTrue(loginFailed, "User login should fail with invalid credentials");
        assertNotNull(guest, "Guest user should be created");
        assertTrue(shoppingCart.getTotalPrice() != 0, "Guest user should be able to add products to the cart");
    }










}




