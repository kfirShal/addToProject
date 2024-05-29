package com.amazonas.business.stores.factories;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.stores.StoresController;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.stores.reservations.ReservationMonitor;
import com.amazonas.utils.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component("storeCallbackFactory")
public class StoreCallbackFactory {

    private final StoresController storesController;
    private final ReservationMonitor reservationMonitor;

    public StoreCallbackFactory(StoresController storesController, ReservationMonitor reservationMonitor) {
        this.storesController = storesController;
        this.reservationMonitor = reservationMonitor;
    }

    public Function<List<Pair<Product,Integer>>, Double> calculatePrice(String storeId){
        return products -> storesController.getStore(storeId).calculatePrice(products);
    }

    public Function<String,Integer> availableCount(String storeId){
        return productId -> storesController.getStore(storeId).availableCount(productId);
    }

    public Function<Map<Product,Integer>, Reservation> makeReservation(String storeId, String userId){
        return products -> storesController.getStore(storeId).reserveProducts(userId,products);
    }

    public Runnable cancelReservation(String storeId, String userId){
        return () -> storesController.getStore(storeId).cancelReservation(userId);
    }

}
