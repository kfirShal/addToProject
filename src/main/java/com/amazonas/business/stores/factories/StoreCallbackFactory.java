package com.amazonas.business.stores.factories;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.stores.StoresController;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.stores.reservations.PendingReservationMonitor;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component("storeCallbackFactory")
public class StoreCallbackFactory {

    private final StoresController storesController;

    public StoreCallbackFactory(StoresController storesController) {
        this.storesController = storesController;
    }

    public Function<Map<String,Integer>, Double> calculatePrice(String storeId){
        return products -> storesController.getStore(storeId).calculatePrice(products);
    }

    public Function<String,Integer> availableCount(String storeId){
        return productId -> storesController.getStore(storeId).availableCount(productId);
    }

    public Function<Map<String,Integer>, Reservation> makeReservation(String storeId, String userId){
        return products -> storesController.getStore(storeId).reserveProducts(products,userId);
    }

    public Function<Reservation,Boolean> cancelReservation(String storeId){
        return reservation ->{
            storesController.getStore(storeId).cancelReservation(reservation);
            return null;
        };
    }

}
