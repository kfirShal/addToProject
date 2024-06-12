package com.amazonas.business.userProfiles;

import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.exceptions.PurchaseFailedException;
import com.amazonas.exceptions.ShoppingCartException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ShoppingCartTest {
    private final String STORE_ID = "storeId";
    private final String PRODUCT_ID = "productId";
    private  StoreBasketFactory storeBasketFactory;
    private ShoppingCart cart;
    private StoreBasket storeBasket;
    @BeforeEach
    void setUp() {
        storeBasketFactory = mock(StoreBasketFactory.class);
        storeBasket = mock(StoreBasket.class);
        String USER_ID = "userId";
        cart = new ShoppingCart(storeBasketFactory, USER_ID);
    }

    @Test
    void mergeGuestCartWithRegisteredCartGood() throws ShoppingCartException {
        String GUEST_USER_ID = "guestUserId";
        ShoppingCart guestCart = new ShoppingCart(storeBasketFactory, GUEST_USER_ID);
        StoreBasket guestStoreBasket = mock(StoreBasket.class);
        when(storeBasketFactory.get(STORE_ID)).thenReturn(guestStoreBasket);
        doNothing().when(guestStoreBasket).addProduct(PRODUCT_ID, 1);
        guestCart.addProduct(STORE_ID, PRODUCT_ID, 1);
        assertNotNull(cart.mergeGuestCartWithRegisteredCart(guestCart));
    }

    @Test
    void getTotalPriceGood() throws ShoppingCartException {
        when(storeBasketFactory.get(STORE_ID)).thenReturn(storeBasket);
        doNothing().when(storeBasket).addProduct(PRODUCT_ID, 1);
        cart.addProduct(STORE_ID, PRODUCT_ID, 1);
        when(storeBasket.getTotalPrice()).thenReturn(100.0);
        assertEquals(100.0, cart.getTotalPrice());
    }

    @Test
    void reserveCartGood() throws ShoppingCartException, PurchaseFailedException {
        when(storeBasketFactory.get(STORE_ID)).thenReturn(storeBasket);
        doNothing().when(storeBasket).addProduct(PRODUCT_ID, 1);
        cart.addProduct(STORE_ID, PRODUCT_ID, 1);
        when(storeBasket.reserveBasket()).thenReturn(mock(Reservation.class));
        assertNotNull(cart.reserveCart());
    }
    @Test
    void reserveCartEmptyCart() {
        assertThrows(PurchaseFailedException.class, () -> cart.reserveCart());
    }
    @Test
    void reserveCartAlreadyReserved() throws ShoppingCartException, PurchaseFailedException {
        when(storeBasketFactory.get(STORE_ID)).thenReturn(storeBasket);
        doNothing().when(storeBasket).addProduct(PRODUCT_ID, 1);
        cart.addProduct(STORE_ID, PRODUCT_ID, 1);
        when(storeBasket.reserveBasket()).thenReturn(mock(Reservation.class));
        cart.reserveCart();
        assertThrows(PurchaseFailedException.class, () -> cart.reserveCart());
    }
    @Test
    void reserveCartSomeProductsNotReserved() throws ShoppingCartException {
        when(storeBasketFactory.get(STORE_ID)).thenReturn(storeBasket);
        doNothing().when(storeBasket).addProduct(PRODUCT_ID, 1);
        cart.addProduct(STORE_ID, PRODUCT_ID, 1);
        when(storeBasket.reserveBasket()).thenReturn(null);
        assertThrows(PurchaseFailedException.class, () -> cart.reserveCart());
    }




}