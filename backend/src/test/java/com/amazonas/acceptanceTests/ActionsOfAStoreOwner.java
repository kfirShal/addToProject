package com.amazonas.acceptanceTests;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.backend.business.notifications.NotificationController;
import com.amazonas.backend.business.payment.PaymentService;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.backend.business.permissions.proxies.StoreProxy;
import com.amazonas.backend.business.stores.StoresController;
import com.amazonas.backend.business.stores.factories.StoreCallbackFactory;
import com.amazonas.backend.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.backend.business.stores.reservations.ReservationFactory;
import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.backend.business.stores.storePositions.StorePosition;
import com.amazonas.backend.business.stores.storePositions.StoreRole;
import com.amazonas.backend.business.userProfiles.Guest;
import com.amazonas.backend.business.userProfiles.ShoppingCartFactory;
import com.amazonas.backend.business.userProfiles.StoreBasketFactory;
import com.amazonas.backend.business.userProfiles.UsersController;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.backend.repository.*;
import com.amazonas.backend.repository.mongoCollections.*;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ActionsOfAStoreOwner {

    private final StoreRepository storeRepository;
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
    private StoreProxy storeProxy;
    private NotificationController notificationController;
    private ProductRepository productRepository;

    public ActionsOfAStoreOwner(StoreMongoCollection storeMongo){
        storeRepository = new StoreRepository(storeMongo);
    }


    @BeforeEach
    public void setup() {

        shoppingCartRepository = new ShoppingCartRepository(shoppingCartMongo);
        shoppingCartFactory = new ShoppingCartFactory(storeBasketFactory);
        storeBasketFactory = new StoreBasketFactory(storeCallbackFactory);
        storeCallbackFactory = new StoreCallbackFactory(storeRepository);
        userRepository = new UserRepository(userMongo);
        authenticationController = new AuthenticationController(userCredentialsRepository);
        storeProxy = new StoreProxy(storesController, permissionsController, authenticationController);

        usersController = new UsersController(
                userRepository,
                new ReservationRepository(),
                new TransactionRepository(transMongo),
                new ProductRepository(productMongo),
                new PaymentService(),
                shoppingCartFactory,
                authenticationController,
                shoppingCartRepository,
                permissionsController,
                notificationController,
                storeRepository
        );
    }


    //-------------------------Inventory management-------------------------

    @Test
    void testInventoryManagement_PositiveCase() {
        // Arrange
        String storeManagerUserId = "storeManager1";
        String validToken = "validToken";
        ProductInventory inventory = new ProductInventory(productRepository);
        Product product = new Product("product1", "LAPTOP", 100.0, "Technologies", "PC", rating.FIVE_STARS, "store1");

        // Act
        try {
            storeProxy.addProduct("store1", product, storeManagerUserId, validToken);
        } catch (StoreException e) {
            throw new RuntimeException(e);
        } catch (AuthenticationFailedException e) {
            throw new RuntimeException(e);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }

        // Assert
        assertTrue(inventory.nameExists(product.getProductName()));
    }

    @Test
    void testInventoryManagement_NegativeCase_GuestUser() {
        // Arrange
        String guestUserId = "guest";
        String validToken = "invalidToken";
        Product newProduct = new Product("product1", "LAPTOP", 100.0, "Technologies", "PC", rating.FIVE_STARS, "store1");

        // Act
        AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class,
                () -> storeProxy.addProduct("storeId", newProduct, guestUserId, validToken));

        // Assert
        assertTrue(exception.getMessage().contains("Authentication failed"));
    }

    @Test
    void testInventoryManagement_AlternativeCase_RequestPermissions() {
        // Arrange
        String userId = "user1";
        String validToken = "validToken";
        Product newProduct = new Product("product1", "LAPTOP", 100.0, "Technologies", "PC", rating.FIVE_STARS, "store1");

        // Act
        NoPermissionException exception = assertThrows(NoPermissionException.class,
                () -> storeProxy.addProduct("storeId", newProduct, userId, validToken));

        // Assert
        assertTrue(exception.getMessage().contains("No permission to add product to store"));
        // User can proceed to request permissions from the owners' team
    }

    //-------------------------Appointing store owner-------------------------

    @Test
    void testAppointStoreOwner_PositiveCase() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String newOwnerUserId = "newOwner";
        String validToken = "validToken";

        // Act
        try {
            storeProxy.addOwner(storeOwnerUserId, "store1", newOwnerUserId, storeOwnerUserId, validToken);
            storeProxy.addOwner(newOwnerUserId, "store1", storeOwnerUserId, newOwnerUserId, validToken);
        } catch (StoreException | AuthenticationFailedException | NoPermissionException e) {
            throw new RuntimeException(e);
        }

        // Assert
        try {
            assertTrue(storeProxy.getStoreRolesInformation("store1", newOwnerUserId, validToken).contains(StoreRole.STORE_MANAGER));
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        } catch (AuthenticationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAppointStoreOwner_NegativeCase_AlreadyOwner() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String existingOwnerUserId = "existingOwner";
        String validToken = "validToken";

        // Act and Assert
        StoreException exception = assertThrows(StoreException.class, () -> {
            storeProxy.addOwner(storeOwnerUserId, "store1", existingOwnerUserId, storeOwnerUserId, validToken);
        });

        // Assert
        assertTrue(exception.getMessage().contains("already an owner"));
    }

    @Test
    void testAppointStoreOwner_AlternativeCase_RepeatRequest() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String newOwnerUserId = "newOwner";
        String validToken = "validToken";

        // Act
        try {
            storeProxy.addOwner(storeOwnerUserId, "store1", newOwnerUserId, storeOwnerUserId, validToken);
            // Simulate newOwnerUserId not approving the request
        } catch (StoreException | AuthenticationFailedException | NoPermissionException e) {
            throw new RuntimeException(e);
        }

        // Assert: Owner can send a repeat request
        try {
            assertFalse(storeProxy.getStoreRolesInformation("store1", newOwnerUserId, validToken).contains(StoreRole.STORE_MANAGER));
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        } catch (AuthenticationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    //-------------------------Appointing store manager-------------------------

    @Test
    void testAppointStoreManager_PositiveCase() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String newManagerUserId = "newManager";
        String validToken = "validToken";

        // Act
        try {
            storeProxy.addManager(storeOwnerUserId, "store1", newManagerUserId, storeOwnerUserId, validToken);
            storeProxy.addManager(newManagerUserId, "store1", storeOwnerUserId, newManagerUserId, validToken);
        } catch (StoreException | AuthenticationFailedException | NoPermissionException e) {
            throw new RuntimeException(e);
        }

        // Assert
        try {
            assertTrue(storeProxy.getStoreRolesInformation("store1", newManagerUserId, validToken).contains(StoreRole.STORE_MANAGER));
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        } catch (AuthenticationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testAppointStoreManager_NegativeCase_AlreadyManagerOrOwner() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String existingManagerUserId = "existingManager";
        String validToken = "validToken";

        // Act and Assert
        StoreException exception = assertThrows(StoreException.class, () -> {
            storeProxy.addManager(storeOwnerUserId, "store1", existingManagerUserId, storeOwnerUserId, validToken);
        });

        // Assert
        assertTrue(exception.getMessage().contains("already an owner or manager"));
    }

    @Test
    void testAppointStoreManager_AlternativeCase_RepeatRequest() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String newManagerUserId = "newManager";
        String validToken = "validToken";

        // Act
        try {
            storeProxy.addManager(storeOwnerUserId, "store1", newManagerUserId, storeOwnerUserId, validToken);
            // Simulate newManagerUserId not approving the request
        } catch (StoreException | AuthenticationFailedException | NoPermissionException e) {
            throw new RuntimeException(e);
        }

        // Assert
        try {
            assertFalse(storeProxy.getStoreRolesInformation("store1", newManagerUserId, validToken).contains(StoreRole.STORE_MANAGER));
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        } catch (AuthenticationFailedException e) {
            throw new RuntimeException(e);
        }
    }

    //-------------------------Change Permissions-------------------------

    @Test
    void testChangePermissions_PositiveCase() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String storeManagerUserId = "storeManager1";
        String validToken = "validToken";

        // Act
        try {
            storeProxy.addPermissionToManager("store1", storeManagerUserId, StoreActions.UPDATE_PRODUCT, storeOwnerUserId, validToken);
        } catch (StoreException | AuthenticationFailedException | NoPermissionException e) {
            throw new RuntimeException(e);
        }

        // Assert
        assertTrue(storeManagerUserId.equals("manager"));
    }

    @Test
    void testChangePermissions_NegativeCase_NonExistentManager() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String nonExistentManagerUserId = "nonExistentManager";
        String validToken = "validToken";

        // Act and Assert
        StoreException exception = assertThrows(StoreException.class, () -> {
            storeProxy.addPermissionToManager("store1", nonExistentManagerUserId, StoreActions.UPDATE_PRODUCT, storeOwnerUserId, validToken);
        });

        // Assert
        assertTrue(exception.getMessage().contains("could not be found"));
    }

    @Test
    void testChangePermissions_AlternativeCase_PermissionLimitation() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String storeManagerUserId = "storeManager1";
        String validToken = "validToken";

        // Assume storeOwner1 has limited permissions and cannot assign certain actions

        // Act
        StoreException exception = assertThrows(StoreException.class, () -> {
            storeProxy.addPermissionToManager("store1", storeManagerUserId, StoreActions.CLOSE_STORE, storeOwnerUserId, validToken);
        });

        // Assert
        assertTrue(exception.getMessage().contains("permission limitation"));
    }

    //-------------------------closing store-------------------------

    @Test
    void testClosingStore_PositiveCase() {
        // Arrange
        String storeFounderUserId = "storeFounder1";
        String validToken = "validToken";

        // Act
        try {
            storeProxy.closeStore("store1", storeFounderUserId, validToken);
        } catch (StoreException | AuthenticationFailedException | NoPermissionException e) {
            throw new RuntimeException(e);
        }

        // Assert
        try {
            assertFalse(storeProxy.openStore("store1", storeFounderUserId, validToken));
        } catch (StoreException e) {
            throw new RuntimeException(e);
        } catch (AuthenticationFailedException e) {
            throw new RuntimeException(e);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testClosingStore_NegativeCase_AlreadyClosed() {
        // Arrange
        String storeFounderUserId = "storeFounder1";
        String validToken = "validToken";

        // Act and Assert
        StoreException exception = assertThrows(StoreException.class, () -> {
            storeProxy.closeStore("store1", storeFounderUserId, validToken);
        });

        // Assert
        assertTrue(exception.getMessage().contains("already closed"));
    }

    @Test
    void testClosingStore_AlternativeCase_NotLoggedIn() {
        // Arrange
        String storeFounderUserId = "storeFounder1";
        String validToken = "invalidToken";  // Assuming invalid token for not logged in

        // Act and Assert
        AuthenticationFailedException exception = assertThrows(AuthenticationFailedException.class, () -> {
            storeProxy.closeStore("store1", storeFounderUserId, validToken);
        });

        // Assert
        assertTrue(exception.getMessage().contains("authentication failed"));
    }

    //-------------------------View workers and permissions -------------------------

    @Test
    void testViewWorkersAndPermissions_PositiveCase() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String validToken = "validToken";

        // Act
        List<StorePosition> positions;
        try {
            positions = storeProxy.getStoreRolesInformation("store1", storeOwnerUserId, validToken);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            throw new RuntimeException(e);
        }

        // Assert: Verify positions list contains store managers with their permissions
        assertFalse(positions.isEmpty());
        // Additional assertions based on the actual returned data
    }

    @Test
    void testViewWorkersAndPermissions_NegativeCase_NoManagers() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String validToken = "validToken";

        // Act and Assert
        StoreException exception = assertThrows(StoreException.class, () -> {
            storeProxy.getStoreRolesInformation("store1", storeOwnerUserId, validToken);
        });

        // Assert
        assertTrue(exception.getMessage().contains("no managers available"));
    }

    @Test
    void testViewWorkersAndPermissions_AlternativeCase_OtherWorkers() {
        // Arrange
        String storeOwnerUserId = "storeOwner1";
        String validToken = "validToken";

        // Act
        List<StorePosition> positions;
        try {
            positions = storeProxy.getStoreRolesInformation("store1", storeOwnerUserId, validToken);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            throw new RuntimeException(e);
        }

        // Assert: Verify positions list contains all roles' permissions storeOwner1 can view
        assertFalse(positions.isEmpty());
        // Additional assertions based on the actual returned data
    }
    //-------------------------purchase history-------------------------

}
