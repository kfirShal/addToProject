package com.amazonas.integrationTests;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.backend.business.notifications.NotificationController;
import com.amazonas.backend.business.payment.PaymentService;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.business.stores.factories.StoreCallbackFactory;
import com.amazonas.backend.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.backend.business.stores.reservations.ReservationFactory;
import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.backend.business.userProfiles.*;
import com.amazonas.backend.repository.*;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("FieldCanBeLocal")
public class PurchaseTests {

    // ================== Constants ================== |
    private static final String STORE_ID = "storeId";
    private static final String USER_ID = "userId";
    private static final String PRODUCT_ID = "productId";
    // ================================================= |

    // ===================== Mocks ===================== |
    private UserRepository userRepository;
    private AuthenticationController authenticationController;
    private ReservationRepository reservationRepository;
    private ShoppingCartRepository shoppingCartRepository;
    private ProductRepository productRepository;
    private PaymentService paymentService;
    private AppointmentSystem appointmentSystem;
    private PendingReservationMonitor pendingReservationMonitor;
    private PermissionsController permissionsController;
    private ReservationFactory reservationFactory;
    private TransactionRepository transactionRepository;
    private StoreRepository storeRepository;
    private NotificationController notificationController;
    // ================================================= |

    // ================== Real instances =============== |
    private StoreCallbackFactory storeCallbackFactory;
    private StoreBasketFactory storeBasketFactory;
    private ShoppingCartFactory shoppingCartFactory;
    private Store store;
    private ProductInventory inventory;
    private UsersController usersController;
    private ShoppingCart shoppingCart;
    private StoreBasket basket;
    private Product product;
    // ================================================= |

    @BeforeEach
    void setUp() {

        // ============= Store setup ============= |
        // Mocks
        appointmentSystem = mock(AppointmentSystem.class);
        pendingReservationMonitor = mock(PendingReservationMonitor.class);
        permissionsController = mock(PermissionsController.class);
        reservationFactory = mock(ReservationFactory.class);
        transactionRepository = mock(TransactionRepository.class);
        // Real instances
        inventory = new ProductInventory();
        store = new Store(
                "storeId",
                "storeName",
                "storeDescription",
                Rating.FIVE_STARS,
                inventory,
                appointmentSystem,
                reservationFactory,
                pendingReservationMonitor,
                permissionsController,
                transactionRepository);

        // ============= UsersController setup ============= |
        // Mocks
        userRepository = mock(UserRepository.class);
        productRepository = mock(ProductRepository.class);
        authenticationController = mock(AuthenticationController.class);
        storeRepository = mock(StoreRepository.class);
        paymentService = mock(PaymentService.class);
        reservationRepository = mock(ReservationRepository.class);
        shoppingCartRepository = mock(ShoppingCartRepository.class);
        notificationController = mock(NotificationController.class);
        // real instances
        storeCallbackFactory = new StoreCallbackFactory(storeRepository);
        storeBasketFactory = new StoreBasketFactory(storeCallbackFactory);
        shoppingCartFactory = new ShoppingCartFactory(storeBasketFactory);
        usersController = new UsersController(
                userRepository,
                reservationRepository,
                transactionRepository,
                productRepository,
                paymentService,
                shoppingCartFactory,
                authenticationController,
                shoppingCartRepository,
                permissionsController,
                notificationController,
                storeRepository);

        // ============= Entities setup ============= |
        shoppingCart = new ShoppingCart(storeBasketFactory, USER_ID);
        product = new Product(PRODUCT_ID, "productName", 10.0, "category", "description", Rating.FIVE_STARS);

        // ============== Mocks configuration ============== |
        when(storeRepository.getStore(STORE_ID)).thenReturn(store);
        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(shoppingCart);
        when(productRepository.getProduct(PRODUCT_ID)).thenReturn(product);

    }

    @Test
    public void testPurchaseFailsDueToRejectedPayment(){
        // ================== Test setup ================== |
        // make payment fail
        when(paymentService.charge(any(),any())).thenReturn(false);
        // add product to inventory to be able to add it to the cart
        assertDoesNotThrow(()->inventory.addProduct(product));
        assertDoesNotThrow(()->store.updateProduct(product));













        // Test execution

    }


    @SuppressWarnings("unchecked")
    private <T,K> K getField(T object, String fieldName){
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return (K) field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

