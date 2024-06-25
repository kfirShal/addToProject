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
import com.amazonas.backend.exceptions.PurchaseFailedException;
import com.amazonas.backend.repository.*;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.utils.Rating;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
    private ReservationRepository reservationRepository;
    private StoreCallbackFactory storeCallbackFactory;
    private StoreBasketFactory storeBasketFactory;
    private ShoppingCartFactory shoppingCartFactory;
    private Store store;
    private ProductInventory inventory;
    private UsersController usersController;
    private ShoppingCart shoppingCart;
    private Product product;
    private User user;
    // ================================================= |

    @BeforeEach
    void setUp() {

        // ============= Store setup ============= |
        // Mocks
        appointmentSystem = mock(AppointmentSystem.class);
        pendingReservationMonitor = mock(PendingReservationMonitor.class);
        permissionsController = mock(PermissionsController.class);
        transactionRepository = mock(TransactionRepository.class);
        shoppingCartRepository = mock(ShoppingCartRepository.class);
        storeRepository = mock(StoreRepository.class);
        // Real instances
        storeCallbackFactory = new StoreCallbackFactory(storeRepository);
        reservationFactory = new ReservationFactory(storeCallbackFactory, shoppingCartRepository);
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
        paymentService = mock(PaymentService.class);
        notificationController = mock(NotificationController.class);
        // real instances
        reservationRepository = spy(new ReservationRepository());
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
        user = new RegisteredUser(USER_ID, "email@email.com");

        // ============== Mocks configuration ============== |
        when(storeRepository.getStore(STORE_ID)).thenReturn(store);
        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(shoppingCart);
        when(productRepository.getProduct(PRODUCT_ID)).thenReturn(product);
        when(userRepository.getUser(USER_ID)).thenReturn(user);

    }

    @Test
    public void testPurchaseFailsDueToRejectedPayment(){
        // ================== Test setup ================== |
        when(paymentService.charge(any(),any())).thenReturn(false); // payment will fail
        assertDoesNotThrow(()->store.addProduct(product));
        assertDoesNotThrow(()->store.setProductQuantity(PRODUCT_ID, 10));
        assertDoesNotThrow(()->usersController.addProductToCart(USER_ID, STORE_ID, PRODUCT_ID, 5));
        Map<String,StoreBasket> baskets = getField(shoppingCart, "baskets");
        StoreBasket basket = baskets.get(STORE_ID);
        // check that the basket was created
        assertNotNull(basket);
        // start the purchase
        assertDoesNotThrow(()->usersController.startPurchase(USER_ID));
        // check that the product is in the basket
        assertEquals(5, basket.getProducts().get(PRODUCT_ID));
        // check that the store basket was reserved
        assertTrue(basket.isReserved());
        // check that the product quantity was reserved
        assertEquals(5, assertDoesNotThrow(()->store.availableCount(PRODUCT_ID)));
        // check that the reservation was saved
        verify(reservationRepository,times(1)).saveReservation(any(),any());

        // ================== Test execution ================== |
        assertThrows(PurchaseFailedException.class, ()-> usersController.payForPurchase(USER_ID));

        // ================== Test verification ================== |
        // check that the payment was attempted
        verify(paymentService,times(1)).charge(any(),any());
        // check that the product remained in the basket
        assertEquals(5, basket.getProducts().get(PRODUCT_ID));
        // check that the transaction was not created
        verify(transactionRepository,times(0)).addNewTransaction(any());
        // check that the notification was not sent
        assertDoesNotThrow(()-> verify(notificationController, times(0)).sendNotification(any(),any(),any(),any()));
        // check that the cart was not reset
        verify(shoppingCartRepository,times(0)).saveCart(any());
        // check that the product quantity returned to the initial value
        assertEquals(10, assertDoesNotThrow(()->store.availableCount(PRODUCT_ID)));
        // check that the store basket reservation flag was reset
        assertFalse(basket.isReserved());
    }

    @Test
    public void testPurchaseFailsDueToInsufficientStock(){
        // ================== Test setup ================== |
        when(paymentService.charge(any(),any())).thenReturn(false); // payment will fail
        assertDoesNotThrow(()->store.addProduct(product));
        assertDoesNotThrow(()->store.setProductQuantity(PRODUCT_ID, 3));
        assertDoesNotThrow(()->usersController.addProductToCart(USER_ID, STORE_ID, PRODUCT_ID, 5));
        Map<String,StoreBasket> baskets = getField(shoppingCart, "baskets");
        StoreBasket basket = baskets.get(STORE_ID);
        assertNotNull(basket); // check that the basket was created

        // ================== Test execution ================== |
        assertThrows(PurchaseFailedException.class, ()->usersController.startPurchase(USER_ID));

        // ================== Test verification ================== |
        // check that the product remained in the basket
        assertEquals(5, basket.getProducts().get(PRODUCT_ID));
        // check that the product quantity was not updated
        assertEquals(3, assertDoesNotThrow(()->store.availableCount(PRODUCT_ID)));
        // check that the store basket was not reserved
        assertFalse(basket.isReserved());
        // check that no reservations were saved
        verify(reservationRepository, times(0)).saveReservation(any(),any());
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

