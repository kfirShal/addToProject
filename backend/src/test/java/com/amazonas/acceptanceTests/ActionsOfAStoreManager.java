package com.amazonas.acceptanceTests;
import com.amazonas.business.permissions.profiles.PermissionsProfile;
import com.amazonas.business.permissions.proxies.StoreProxy;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.stores.StoresController;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.stores.factories.StoreFactory;
import com.amazonas.business.stores.search.SearchRequest;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.repository.PermissionsProfileRepository;
import com.amazonas.repository.StoreRepository;
import com.amazonas.repository.TransactionRepository;
import com.amazonas.repository.UserCredentialsRepository;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ActionsOfAStoreManager {

    private StoreProxy storeProxy;
    private Rating rating;
    private UserCredentialsRepository userCredentialsRepository;
    private PermissionsProfile defaultRegisteredUserPermissionsProfile;
    private PermissionsProfile guestPermissionsProfile;
    private PermissionsProfileRepository permissionsProfileRepository;
    private StoreFactory storeFactory;
    private StoreRepository storeRepository;
    private TransactionRepository transactionRepository;
    private StoresController storesController;

    @BeforeEach
    public void setUp() {
        PermissionsController permissionsController = new PermissionsController(defaultRegisteredUserPermissionsProfile, guestPermissionsProfile, permissionsProfileRepository);
        AuthenticationController authenticationController = new AuthenticationController(userCredentialsRepository);
        StoresController storesController = new StoresController(storeFactory, storeRepository, transactionRepository);
        storeProxy = new StoreProxy(storesController, permissionsController, authenticationController);
    }

    //-------------------------permission check-------------------------

    @Test
    public void testAddProduct_ValidPermissions_Success() {
        // Arrange
        String storeId = "store1";
        String userId = "user1";
        String token = "validToken";
        Product productToAdd = new Product("product1", "LAPTOP", 100.0, "Technologies", "PC", rating.FIVE_STARS);
        SearchRequest request = new SearchRequest(productToAdd.productName(), new ArrayList<>(), 0, 200, productToAdd.category(), productToAdd.rating());

        // Act
        assertTrue(storesController.searchProductsInStore(storeId, request).contains(productToAdd));
    }

    @Test
    public void testAddProduct_InvalidPermissions_NoPermissionException() {
        // Arrange
        String storeId = "store1";
        String userId = "user1";
        String token = "invalidToken";
        Product productToAdd = new Product("product1", "LAPTOP", 100.0, "Technologies", "PC", rating.FIVE_STARS);

        // Act and Assert
        NoPermissionException exception = assertThrows(NoPermissionException.class,
                () -> storeProxy.addProduct(storeId, productToAdd, userId, token));

        // Assert: Verify the exception message or details if needed
        assertTrue(exception.getMessage().contains("No permission to add product"));
    }

}
