package com.amazonas.business.stores.reservations;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.stores.factories.StoreCallbackFactory;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.exceptions.ShoppingCartException;
import com.amazonas.repository.ShoppingCartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

@Component("reservationFactory")
public class ReservationFactory {


    private static final Logger log = LoggerFactory.getLogger(ReservationFactory.class);

    private final StoreCallbackFactory storeCallbackFactory;
    private final ShoppingCartRepository shoppingCartRepository;

    public ReservationFactory(StoreCallbackFactory storeCallbackFactory, ShoppingCartRepository shoppingCartRepository) {
        this.storeCallbackFactory = storeCallbackFactory;
        this.shoppingCartRepository = shoppingCartRepository;
    }

    public Reservation get(String userId,
                           String storeId,
                           Map<String, Integer> productToQuantity,
                           LocalDateTime expirationDate){

        ShoppingCart shoppingCart = shoppingCartRepository.getCart(userId);
        Runnable unReserveBasket = () -> {
            try {
                shoppingCart.unReserve(storeId);
            } catch (ShoppingCartException e) {
                log.error("this should not happen: Failed to unreserve basket", e);
                throw new IllegalStateException("this should not happen: Failed to unreserve basket");
            }
        };

        return new Reservation(
                userId,
                UUID.randomUUID().toString(),
                storeId,
                productToQuantity,
                expirationDate,
                storeCallbackFactory.cancelReservation(storeId),
                unReserveBasket);
    }
}
