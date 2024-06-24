package com.amazonas.backend.acceptanceTests;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.permissions.profiles.PermissionsProfile;
import com.amazonas.backend.business.permissions.proxies.StoreProxy;
import com.amazonas.backend.business.stores.StoresController;
import com.amazonas.backend.business.stores.factories.StoreFactory;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.repository.PermissionsProfileRepository;
import com.amazonas.backend.repository.StoreRepository;
import com.amazonas.backend.repository.TransactionRepository;
import com.amazonas.backend.repository.UserCredentialsRepository;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.requests.stores.SearchRequest;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ActionsOfAStoreManager {

    private StoreProxy storeProxy;
    private Rating rating;
    private UserCredentialsRepository userCredentialsRepository;
    private PermissionsProfile defaultRegisteredUserPermissionsProfile;
    private PermissionsProfile guestPermissionsProfile;
    private PermissionsProfile adminPermissionsProfile;
    private PermissionsProfileRepository permissionsProfileRepository;
    private StoreFactory storeFactory;
    private StoreRepository storeRepository;
    private TransactionRepository transactionRepository;
    private StoresController storesController;

    @BeforeEach
    public void setUp() {
        PermissionsController permissionsController = new PermissionsController(defaultRegisteredUserPermissionsProfile, guestPermissionsProfile,adminPermissionsProfile, permissionsProfileRepository);
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
