package com.amazonas.common.dtos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class StoreBasket {
    private static final Logger log = LoggerFactory.getLogger(StoreBasket.class);
    private final Map<String, Integer> products;
    private final Function<Map<String, Integer>, Reservation> makeReservation;
    private final Function<Map<String, Integer>, Double> calculatePrice;
    private boolean reserved;

    public StoreBasket(Function<Map<String, Integer>, Reservation> makeReservation, Function<Map<String, Integer>, Double> calculatePrice) {
        this.makeReservation = makeReservation;
        this.calculatePrice = calculatePrice;
        products = new HashMap<>();
    }

    public Map<String, Integer> getProducts() {
        return products;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void changeProductQuantity(String productId, int quantity) throws Exception {
        if (quantity <= 0) {
            throw new Exception("Quantity cannot be 0 or less");
        }
        if (!products.containsKey(productId)) {
            throw new Exception("Product with id: " + productId + " not found");
        }
        products.put(productId, quantity);
    }

    public boolean makeReservation() {
        if (reserved) {
            return false;
        }
        makeReservation.apply(products);
        reserved = true;
        return true;
    }

    public double calculatePrice() {
        return calculatePrice.apply(products);
    }
  }
