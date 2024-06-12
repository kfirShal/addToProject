package com.amazonas.business.userProfiles;

import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.exceptions.ShoppingCartException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

class StoreBasketTest {
    private final String PRODUCT_ID = "productId";
    private Function<Map<String,Integer>, Reservation> makeReservation;
    private Function<Map<String,Integer>, Double> calculatePrice;
    private StoreBasket storeBasket;

    @BeforeEach
    void setUp() {
        makeReservation = mock(Function.class);
        calculatePrice = mock(Function.class);
        storeBasket = new StoreBasket(makeReservation, calculatePrice);
    }

    @Test
    void addProductGood() {
        assertDoesNotThrow(() -> storeBasket.addProduct(PRODUCT_ID, 1));
    }
    @Test
    void addProductInvalidQuantity() {
        assertThrows(ShoppingCartException.class, () -> storeBasket.addProduct(PRODUCT_ID, 0));
    }
    @Test
    void addProductAlreadyExists() throws ShoppingCartException {
        storeBasket.addProduct(PRODUCT_ID, 1);
        assertThrows(ShoppingCartException.class, () -> storeBasket.addProduct(PRODUCT_ID, 1));
    }


    @Test
    void removeProductGood() throws ShoppingCartException {
        storeBasket.addProduct(PRODUCT_ID, 1);
        assertDoesNotThrow(() -> storeBasket.removeProduct(PRODUCT_ID));
    }
    @Test
    void removeProductNotFound() {
        assertThrows(ShoppingCartException.class, () -> storeBasket.removeProduct(PRODUCT_ID));
    }

    @Test
    void changeProductQuantityGood() throws ShoppingCartException {
        storeBasket.addProduct(PRODUCT_ID, 1);
        assertDoesNotThrow(() -> storeBasket.changeProductQuantity(PRODUCT_ID, 2));
    }
    @Test
    void changeProductQuantityInvalidQuantity() throws ShoppingCartException {
        storeBasket.addProduct(PRODUCT_ID, 1);
        assertThrows(ShoppingCartException.class, () -> storeBasket.changeProductQuantity(PRODUCT_ID, -1));

    }
    @Test
    void changeProductQuantityProductNotFound() throws ShoppingCartException {
        assertThrows(ShoppingCartException.class, () -> storeBasket.changeProductQuantity(PRODUCT_ID, 2));

    }
}