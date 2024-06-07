package com.amazonas.business.userProfiles;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.exceptions.PurchaseFailedException;
import com.amazonas.exceptions.ShoppingCartException;
import com.amazonas.exceptions.UserException;
import com.amazonas.repository.ProductRepository;
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
    private UserRepository userRepository;
    private ShoppingCartFactory shoppingCartFactory;
    private  StoreBasketFactory storeBasketFactory;
    private StoreBasket mockBasket;
    private PaymentService paymentService;
    private TransactionRepository transactionRepository;
    private ReservationRepository reservationRepository;
    private AuthenticationController authenticationController;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {

        reservationRepository = mock(ReservationRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        productRepository = mock(ProductRepository.class);
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
                authenticationController);


        product = new Product("productId", "name", 100, "category", "5", Rating.NOT_RATED);
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(shoppingCartFactory.get("testUserName2")).thenReturn(mockCart);
        mockBasket = mock(StoreBasket.class);
        HashMap<String, Pair<String, Integer>> map = new HashMap<>(){{
            put("productId", new Pair<>(product.productId(), 1));
        }};
        when(mockBasket.getProducts()).thenReturn(map);
        when(storeBasketFactory.get("testStoreName")).thenReturn(mockBasket);
        when(productRepository.getProduct(product.productId())).thenReturn(product);
        usersController.register("testEmail@gmail.com", "testUserName", "testPassword@");
        usersController.register("testEmail2@gmail.com", "testUserName2", "testPassword@2");
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
        assertThrows(UserException.class, () -> usersController.register("testEmail7@gmail.com", "testUserName", "testPassword7@"));
    }

    @Test
    void registerFailureIllegalPassword() {
        assertThrows(UserException.class, () -> usersController.register("testEmailr@gmail.com", "testUserName", "testPassword7"));
    }

    @Test
    void registerFailureEmailAlreadyExists() {
        assertThrows(UserException.class, () -> usersController.register("testEmail@gmail.com", "testUserName1", "testPassword7@"));
    }

    @Test
    void registerFailureIllegalEmail() {
        assertThrows(UserException.class, () -> usersController.register("testEmail7@gmaicom", "testUserName2", "testPassword7@"));
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
        assertThrows(UserException.class, () -> usersController.loginToRegistered(guestId, "testUserName1"));
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
        assertThrows(UserException.class, () -> usersController.logout(guestId));
    }

    @Test
    void logoutAsGuestSuccess() {
        String guestId = usersController.enterAsGuest();
        usersController.logoutAsGuest(guestId);
        assertNull(usersController.getGuest(guestId));
    }

    @Test
    void logoutAsGuestFailureUserNotGuest() {
        assertThrows(UserException.class, () -> usersController.logoutAsGuest("wrongId"));
    }

    @Test
    void getCartSuccess() {
        ShoppingCart cart= usersController.getCart("testUserName");
        assertEquals(cart, usersController.getCart("testUserName"));

    }

    @Test
    void getCartFailureWrongId() {
        assertThrows(ShoppingCartException.class, () -> usersController.getCart("wrongId"));
    }

    @Test
    void addProductToCartSuccess() {

        usersController.addProductToCart("testUserName2", "testStoreName", product.productId(), 1);
        int test = usersController.getCart("testUserName2").getBasket("testStoreName").getProducts().size();
        assertEquals(1, test);
    }

    @Test
    void addProductToCartFailureIllegalQuantity() {
        assertThrows(ShoppingCartException.class, () -> usersController.addProductToCart("testUserName", "testStoreName", "productId", -1));
    }

    @Test
    void removeProductFromCartSuccess() {

        //add product to cart
        usersController.addProductToCart("testUserName2", "testStoreName", product.productId(), 1);

        //remove product from cart
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getBasketWithValidation("testUserName2")).thenReturn(mockBasket);
        when(mockBasket.isProductExists("productId")).thenReturn(true);
        usersController.RemoveProductFromCart("testUserName2", "testStoreName", "productId");
        when(mockBasket.getProducts()).thenReturn(new HashMap<>());
        int test = usersController.getCart("testUserName2").getBasket("testStoreName").getProducts().size();
        assertEquals(0, test);

    }

    @Test
    void removeProductFromCartFailure() {
        assertThrows(ShoppingCartException.class, () -> usersController.RemoveProductFromCart("testUserName", "testStoreName", "productId"));
    }

    @Test
    void changeProductQuantitySuccess() {
        //add product to cart
        usersController.addProductToCart("testUserName2", "testStoreName", product.productId(), 1);
        //change product quantity
        ShoppingCart mockCart = mock(ShoppingCart.class);
        when(mockCart.getBasketWithValidation("testUserName2")).thenReturn(mockBasket);
        when(mockBasket.getProductWithQuantity("productId")).thenReturn(new Pair<>(product.productId(), 1));
        HashMap<String, Pair<String, Integer>> newMap = new HashMap<>(){{
            put("productId", new Pair<>(product.productId(),5));
        }};
        when(mockBasket.getProducts()).thenReturn(newMap);
        usersController.changeProductQuantity("testUserName2", "testStoreName", "productId", 5);
        int test = usersController.getCart("testUserName2").getBasket("testStoreName").getProducts().get("productId").second();
        assertEquals(5, test);
    }

    @Test
    void changeProductQuantityFailure() {
        assertThrows(ShoppingCartException.class, () -> usersController.changeProductQuantity("testUserName", "testStoreName", "productId", 5));
    }

    @Test
    void makePurchaseSuccess() throws PurchaseFailedException {
        //TODO: fix this test

        //add product to cart
        usersController.addProductToCart("testUserName2", "testStoreName", product.productId(), 1);
        //prepare mocks
        ShoppingCart mockCart = mock(ShoppingCart.class);
        Reservation reservation = mock(Reservation.class);
        User user = usersController.getRegisteredUser("testUserName2");
        when(userRepository.getUser("testUserName2")).thenReturn(user);
        Map<String, Reservation> reservations = new HashMap<>(){
            {
                put("testStoreName", reservation);
            }
        };
        Transaction transaction = mock(Transaction.class);
        when(mockCart.reserveCart()).thenReturn(reservations);
        when(paymentService.charge(user.getPaymentMethod(),100)).thenReturn(true);
//        when(shippingService.ship(transaction)).thenReturn(true);
        doNothing().when(transactionRepository).addNewTransaction(transaction);
        //make purchase
//        usersController.makePurchase("testUserName2");
//        verify(transactionsController, times(1));

    }
}
