package com.amazonas.business.userProfiles;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.exceptions.PurchaseFailedException;
import com.amazonas.repository.ReservationRepository;
import com.amazonas.repository.TransactionRepository;
import com.amazonas.repository.UserRepository;
import com.amazonas.utils.Pair;
import com.amazonas.utils.Rating;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UsersControllerTest {

    private UsersController usersController;
    private Product product;

    @Mock
    private UserRepository repository;
    private ShoppingCartFactory shoppingCartFactory;
    private  StoreBasketFactory storeBasketFactory;
    private StoreBasket mockBasket;
    private PaymentService paymentService;
    private TransactionRepository transactionRepository;
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {

        reservationRepository = mock(ReservationRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        storeBasketFactory = mock(StoreBasketFactory.class);
        shoppingCartFactory = mock(ShoppingCartFactory.class);
        repository = mock(UserRepository.class);
        paymentService = mock(PaymentService.class);
        usersController = new UsersController(repository, reservationRepository, transactionRepository, paymentService, shoppingCartFactory);
        usersController.register("testEmail@gmail.com", "testUserName", "testPassword@");
        usersController.register("testEmail2@gmail.com", "testUserName2", "testPassword@2");
        product = new Product("productId", "name", 100, "category", "5", Rating.NOT_RATED);
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(shoppingCartFactory.get("testUserName2")).thenReturn(mockCart);
        mockBasket = mock(StoreBasket.class);
        HashMap<String, Pair<Product, Integer>> map = new HashMap<>(){{
            put("productId", new Pair<>(product, 1));
        }};
        when(mockBasket.getProducts()).thenReturn(map);
        when(storeBasketFactory.get("testStoreName","testUserName2")).thenReturn(mockBasket);
        usersController.loginToRegistered("testUserName2", "testUserName2");
    }

    @AfterEach
    void tearDown() {

        usersController = null;
    }

    @Test
    void registerSuccess() {
        usersController.register("testEmail1@gmail.com", "testUserName1", "testPassword1@");
        assertNotNull(usersController.getRegisteredUser("testUserName1"));
    }
    @Test
    void registerFailureUserNameAlreadyExists() {
        //username already exists
        assertThrows(IllegalArgumentException.class, () -> usersController.register("testEmail7@gmail.com", "testUserName", "testPassword7@"));
    }
    @Test
    void registerFailureIllegalPassword() {
        assertThrows(IllegalArgumentException.class, () -> usersController.register("testEmailr@gmail.com", "testUserName", "testPassword7"));
    }
    @Test
    void registerFailureEmailAlreadyExists() {
        assertThrows(IllegalArgumentException.class, () -> usersController.register("testEmail@gmail.com", "testUserName1", "testPassword7@"));
    }
    @Test
    void registerFailureIllegalEmail() {
        assertThrows(IllegalArgumentException.class, () -> usersController.register("testEmail7@gmaicom", "testUserName2", "testPassword7@"));
    }


    @Test
    void enterAsGuestSuccess() {
        String guestId = usersController.enterAsGuest();
        assertNotNull(usersController.getGuest(guestId));

    }
    @Test
    void loginToRegisteredSuccess() {
        String guestId = usersController.enterAsGuest();
        usersController.register("testEmail1@gmail.com", "testUserName1", "testPassword1@");
        usersController.loginToRegistered(guestId, "testUserName1");
        assertNotNull(usersController.getOnlineUser("testUserName1"));
    }
    @Test
    void loginToRegisteredFailureUserNotRegistered() {
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
    void logoutFailureUserNotRegistered() {
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
    void logoutAsGuestFailureUserNotGuest() {
        assertThrows(RuntimeException.class, () -> usersController.logoutAsGuest("wrongId"));
    }

    @Test
    void getCartSuccess() {
        ShoppingCart cart= usersController.getCart("testUserName");
        assertEquals(cart, usersController.getCart("testUserName"));

    }
    @Test
    void getCartFailureWrongId() {
        assertThrows(RuntimeException.class, () -> usersController.getCart("wrongId"));
    }

    @Test
    void addProductToCartSuccess() {

        usersController.addProductToCart("testUserName2", "testStoreName", product, 1);
        int test = usersController.getCart("testUserName2").getBasket("testStoreName").getProducts().size();
        assertEquals(1, test);
    }
    @Test
    void addProductToCartFailureCartNotExists() {
        assertThrows(RuntimeException.class, () -> usersController.addProductToCart("testUserName1", "testStoreName", new Product("productId","name",100,"category","5",Rating.NOT_RATED), 1));
    }
    @Test
    void addProductToCartFailureIllegalQuantity() {
        assertThrows(RuntimeException.class, () -> usersController.addProductToCart("testUserName", "testStoreName", new Product("productId","name",100,"category","5",Rating.NOT_RATED), -1));
    }

    @Test
    void removeProductFromCartSuccess() {

        //add product to cart
        usersController.addProductToCart("testUserName2", "testStoreName", product, 1);

        //remove product from cart
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getBasket("testUserName2")).thenReturn(mockBasket);
        when(mockBasket.isProductExists("productId")).thenReturn(true);
        usersController.RemoveProductFromCart("testUserName2", "testStoreName", "productId");
        when(mockBasket.getProducts()).thenReturn(new HashMap<>());
        int test = usersController.getCart("testUserName2").getBasket("testStoreName").getProducts().size();
        assertEquals(0, test);

    }
    @Test
    void removeProductFromCartFailure() {
        assertThrows(RuntimeException.class, () -> usersController.RemoveProductFromCart("testUserName", "testStoreName", "productId"));
    }

    @Test
    void changeProductQuantitySuccess() {
        //add product to cart
        usersController.addProductToCart("testUserName2", "testStoreName", product, 1);
        //change product quantity
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getBasket("testUserName2")).thenReturn(mockBasket);
        when(mockBasket.getProductWithQuantity("productId")).thenReturn(new Pair<>(product, 1));
        HashMap<String, Pair<Product, Integer>> newMap = new HashMap<>(){{
            put("productId", new Pair<>(product,5));
        }};
        when(mockBasket.getProducts()).thenReturn(newMap);
        usersController.changeProductQuantity("testUserName2", "testStoreName", "productId", 5);
        int test = usersController.getCart("testUserName2").getBasket("testStoreName").getProducts().get("productId").second();
        assertEquals(5, test);
    }
    @Test
    void changeProductQuantityFailure() {
        assertThrows(RuntimeException.class, () -> usersController.changeProductQuantity("testUserName", "testStoreName", "productId", 5));
    }



    @Test
    void makePurchaseSuccess() throws PurchaseFailedException {
        //TODO: fix this test

        //add product to cart
        usersController.addProductToCart("testUserName2", "testStoreName", product, 1);
        //prepare mocks
        ShoppingCart mockCart = mock(ShoppingCart.class);
        Reservation reservation = mock(Reservation.class);
        User user = usersController.getRegisteredUser("testUserName2");
        when(repository.getUser("testUserName2")).thenReturn(user);
        Map<String, Reservation> reservations = new HashMap<>(){
            {
                put("testStoreName", reservation);
            }
        };
        Transaction transaction = mock(Transaction.class);
        when(mockCart.reserveCart()).thenReturn(reservations);
        when(paymentService.charge(user.getPaymentMethod(),100)).thenReturn(true);
//        when(shippingService.ship(transaction)).thenReturn(true);
        doNothing().when(transactionRepository).documentTransaction(transaction);
        //make purchase
//        usersController.makePurchase("testUserName2");
//        verify(transactionsController, times(1));

    }}
