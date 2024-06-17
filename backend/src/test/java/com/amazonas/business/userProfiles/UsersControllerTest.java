package com.amazonas.business.userProfiles;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.exceptions.PurchaseFailedException;
import com.amazonas.exceptions.UserException;
import com.amazonas.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UsersControllerTest {
    private static final String USER_ID = "userId";
    private static final String PASSWORD = "Password12#";
    private static final String EMAIL = "email@post.bgu.ac.il";
    private UsersController usersController;
    private PaymentService paymentService;
    private UserRepository userRepository;
    private ShoppingCartFactory shoppingCartFactory;
    private  StoreBasketFactory storeBasketFactory;
    private TransactionRepository transactionRepository;
    private ReservationRepository reservationRepository;
    private AuthenticationController authenticationController;
    private ProductRepository productRepository;
    private ShoppingCartRepository shoppingCartRepository;
    private ShoppingCart cart;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        productRepository = mock(ProductRepository.class);
        shoppingCartRepository = mock(ShoppingCartRepository.class);
        storeBasketFactory = mock(StoreBasketFactory.class);
        shoppingCartFactory = mock(ShoppingCartFactory.class);
        userRepository = mock(UserRepository.class);
        paymentService = mock(PaymentService.class);
        authenticationController = mock(AuthenticationController.class);
        usersController = new UsersController(
                userRepository,
                reservationRepository,
                transactionRepository,
                productRepository,
                paymentService,
                shoppingCartFactory,
                authenticationController,
                shoppingCartRepository);

        cart = mock(ShoppingCart.class);
    }

    @Test
    void registerGood() {
        when(userRepository.userIdExists(USER_ID)).thenReturn(false);
        assertDoesNotThrow(()-> usersController.register(EMAIL, USER_ID, PASSWORD));
    }

    @Test
    void registerUserIdAlreadyExists() {
        when(userRepository.userIdExists(USER_ID)).thenReturn(true);
        assertThrows(UserException.class, ()-> usersController.register(EMAIL, USER_ID, PASSWORD));
    }

    @Test
    void registerBadEmail() {
        when(userRepository.userIdExists(USER_ID)).thenReturn(false);
        assertThrows(UserException.class, ()-> usersController.register(EMAIL, USER_ID, "badEmail"));
    }

    @Test
    void registerBadPassword() {
        when(userRepository.userIdExists(USER_ID)).thenReturn(false);
        assertThrows(UserException.class, ()-> usersController.register(EMAIL,USER_ID,"badpassword"));
    }

    @Test
    void enterAsGuest() {
        String initialId = usersController.enterAsGuest();
        User guest = usersController.getGuest(initialId);
        assertNotNull(guest);
        assertEquals(guest.getUserId(), initialId);
    }

    @Test
    void loginToRegisteredGood() {
        User user = new RegisteredUser(USER_ID, EMAIL);
        when(userRepository.userIdExists(USER_ID)).thenReturn(true);
        when(userRepository.getUser(USER_ID)).thenReturn(user);
        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(cart);

        assertDoesNotThrow(()-> usersController.loginToRegistered("guestId", USER_ID));
    }

    @Test
    void loginToRegisteredUserDoesNotExist() {
        when(userRepository.userIdExists(USER_ID)).thenReturn(false);
        assertThrows(UserException.class, ()-> usersController.loginToRegistered("guestId", USER_ID));
    }

    @Test
    void logoutGood() {
        User user = mock(User.class);
        when(userRepository.userIdExists(USER_ID)).thenReturn(true);
        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(cart);
        when(userRepository.getUser(USER_ID)).thenReturn(user);
        when(cart.mergeGuestCartWithRegisteredCart(any())).thenReturn(cart);
        when(cart.userId()).thenReturn(USER_ID);

        String guestId = assertDoesNotThrow(()-> usersController.enterAsGuest());
        assertDoesNotThrow(()->usersController.loginToRegistered(guestId, USER_ID));
        String guestId2 = assertDoesNotThrow(()-> usersController.logout(USER_ID));
        assertNotNull(guestId2);
        assertEquals(guestId2, assertDoesNotThrow(()-> usersController.getUser(guestId2).getUserId()));
    }

    @Test
    void logoutUserDoesNotExist() {
        when(userRepository.userIdExists(USER_ID)).thenReturn(false);
        assertThrows(UserException.class, ()-> usersController.logout(USER_ID));
    }

    @Test
    void logoutAsGuestGood() {
        String guestId = usersController.enterAsGuest();
        assertNotNull(guestId);
        assertDoesNotThrow(()-> usersController.logoutAsGuest(guestId));
    }

    @Test
    void logoutAsGuestUserDoesNotExist() {
        assertThrows(UserException.class, ()-> usersController.logout(USER_ID));
    }

    @Test
    void startPurchaseGood() {
        Map<String,Reservation> reservations = Map.of("storeId", mock(Reservation.class));
        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(cart);
        when(assertDoesNotThrow(() -> cart.reserveCart())).thenReturn(reservations);
        assertDoesNotThrow(()-> usersController.startPurchase(USER_ID));
    }

    @Test
    void startPurchaseUserDoesNotExist() {
        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(null);
        assertThrows(UserException.class, ()-> usersController.startPurchase(USER_ID));
    }

    @Test
    void payForPurchaseGood() {
        User user = mock(User.class);
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        Reservation reservation = mock(Reservation.class);
        when(reservation.productIdToQuantity()).thenReturn(Map.of());
        when(reservationRepository.getReservations(USER_ID)).thenReturn(List.of(reservation));
        when(userRepository.userIdExists(USER_ID)).thenReturn(true);
        when(userRepository.getUser(USER_ID)).thenReturn(user);
        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(cart);
        when(cart.getTotalPrice()).thenReturn(10.0);
        when(user.getPaymentMethod()).thenReturn(paymentMethod);
        when(paymentMethod.getDetails()).thenReturn("details");
        when(paymentService.charge(any(),any())).thenReturn(true);

        assertDoesNotThrow(()-> usersController.payForPurchase(USER_ID));
    }

    @Test
    void payForPurchaseChargeFails() {
        User user = mock(User.class);
        PaymentMethod paymentMethod = mock(PaymentMethod.class);
        Reservation reservation = mock(Reservation.class);
        when(reservation.productIdToQuantity()).thenReturn(Map.of());
        when(reservationRepository.getReservations(USER_ID)).thenReturn(List.of(reservation));
        when(userRepository.userIdExists(USER_ID)).thenReturn(true);
        when(userRepository.getUser(USER_ID)).thenReturn(user);
        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(cart);
        when(cart.getTotalPrice()).thenReturn(10.0);
        when(user.getPaymentMethod()).thenReturn(paymentMethod);
        when(paymentMethod.getDetails()).thenReturn("details");
        when(paymentService.charge(any(),any())).thenReturn(false);

        assertThrows(PurchaseFailedException.class, ()-> usersController.payForPurchase(USER_ID));
    }

    @Test
    void payForPurchaseUserDoesNotExist() {
        when(userRepository.getUser(USER_ID)).thenReturn(null);
        assertThrows(UserException.class, ()-> usersController.payForPurchase(USER_ID));
    }

    // ========================================================================================= |
    // ================================ CONCURRENT TESTS ======================================= |
    // ========================================================================================= |

    @SuppressWarnings("unchecked")
    @Test
    void testConcurrentStartPurchase() throws InterruptedException, NoSuchFieldException, IllegalAccessException {

        ShoppingCart cart = new ShoppingCart(storeBasketFactory, USER_ID);
        StoreBasket basket = new StoreBasket(_->mock(Reservation.class), _->0.0);
        Field basketsField = ShoppingCart.class.getDeclaredField("baskets");
        basketsField.setAccessible(true);
        Map<String,StoreBasket> baskets = (Map<String,StoreBasket>) basketsField.get(cart);
        baskets.put("storeId", basket);

        when(userRepository.userIdExists(USER_ID)).thenReturn(true);
        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(cart);

        AtomicInteger counter = new AtomicInteger(0);

        ExecutorService service = Executors.newFixedThreadPool(2);
        Runnable test = () -> {
            try {
                usersController.startPurchase(USER_ID);
            } catch (UserException | PurchaseFailedException e) {
                counter.incrementAndGet();
            }
        };

        service.submit(test);
        service.submit(test);
        service.shutdown();
        service.awaitTermination(1, TimeUnit.SECONDS);

        // Verify that startPurchase was called twice
        verify(shoppingCartRepository, times(2)).getCart(USER_ID);

        // Check that one of the purchases failed
        assertEquals(1, counter.get());
    }

//    @SuppressWarnings("unchecked")
//    @Test
//    void testConcurrentCancelPurchase() throws IllegalAccessException, NoSuchFieldException, InterruptedException {
//
//        // setup
//
//        ShoppingCart cart = new ShoppingCart(storeBasketFactory, USER_ID);
//        StoreBasket basket = new StoreBasket(_->mock(Reservation.class), _->0.0);
//        Field basketsField = ShoppingCart.class.getDeclaredField("baskets");
//        basketsField.setAccessible(true);
//        Map<String,StoreBasket> baskets = (Map<String,StoreBasket>) basketsField.get(cart);
//        baskets.put("storeId", basket);
//        Reservation reservation = new Reservation(USER_ID, "reservationId", "storeId", Map.of(), null, _->true, basket::unReserve);
//
//        Map<String,Reservation> reservations = new HashMap<>(){{put(USER_ID, reservation);}};
//        when(reservationRepository.getReservations(USER_ID)).then(_-> reservations.get(USER_ID));
//        doAnswer(_->{
//            reservations.remove(USER_ID);
//            return null;
//        }).when(reservationRepository).removeReservation(USER_ID, reservation);
//
//
//        when(userRepository.userIdExists(USER_ID)).thenReturn(true);
//        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(cart);
//
//        // test
//
//        AtomicInteger counter = new AtomicInteger(0);
//
//        ExecutorService service = Executors.newFixedThreadPool(2);
//        Runnable test = () -> {
//            try {
//                if(usersController.cancelPurchase(USER_ID)){
//                    counter.incrementAndGet();
//                }
//            } catch (UserException e) {
//                fail("UserException thrown when it shouldn't have been");
//            }
//        };
//
//        service.submit(test);
//        service.submit(test);
//        service.shutdown();
//        service.awaitTermination(1, TimeUnit.SECONDS);
//
//        // Verify that cancelPurchase was called twice
//        verify(reservationRepository, times(2)).getReservations(USER_ID);
//
//        // Check that one of the cancellations failed
//        assertEquals(1, counter.get());
//    }
//
//    @SuppressWarnings("unchecked")
//    @Test
//    void testConcurrentPayForPurchase() throws IllegalAccessException, NoSuchFieldException {
//        ShoppingCart cart = new ShoppingCart(storeBasketFactory, USER_ID);
//        StoreBasket basket = new StoreBasket(_->mock(Reservation.class), _->0.0);
//        Field basketsField = ShoppingCart.class.getDeclaredField("baskets");
//        basketsField.setAccessible(true);
//        Map<String,StoreBasket> baskets = (Map<String,StoreBasket>) basketsField.get(cart);
//        baskets.put("storeId", basket);
//        Reservation reservation = new Reservation(USER_ID,
//                "reservationId",
//                "storeId",
//                Map.of(),
//                null,
//                _->true,
//                basket::unReserve);
//
//
//        when(userRepository.userIdExists(USER_ID)).thenReturn(true);
//        when(shoppingCartRepository.getCart(USER_ID)).thenReturn(cart);
//
//        AtomicInteger counter = new AtomicInteger(0);
//
//        ExecutorService service = Executors.newFixedThreadPool(2);
//        Runnable test = () -> {
//            try {
//                usersController.payForPurchase(USER_ID);
//            } catch (UserException | PurchaseFailedException e) {
//                counter.incrementAndGet();
//            }
//        };
//
//        service.submit(test);
//        service.submit(test);
//        service.shutdown();
//        try {
//            service.awaitTermination(1, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Verify that payForPurchase was called twice
//        verify(paymentService, times(2)).charge(any(),any());
//
//        // Check that one of the payments failed
//        assertEquals(1, counter.get());
//    }
}