package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.utils.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component("storeCallbackFactory")
public class StoreCallbackFactory {

    private final StoresControllerImpl storesController;

    public StoreCallbackFactory(StoresControllerImpl storesController) {
        this.storesController = storesController;
    }

    public Function<List<Pair<Product,Integer>>,Integer> calculatePrice(String storeId){
        final Store store = storesController.getStore(storeId);
        return store::calculatePrice;
    }

    public Function<String,Boolean> isProductAvailable(String storeId){
        final Store store = storesController.getStore(storeId);
        return store::isProductAvailable;
    }

    public Function<List<Pair<Product,Integer>>,Reservation> reserveProducts(String storeId, String userId){
        final Store store = storesController.getStore(storeId);
        return products -> store.reserveProducts(userId, products);
    }

    public Function<String,Void> cancelReservation(String storeId){
        final Store store = storesController.getStore(storeId);
        return reservationId -> {
            store.cancelReservation(reservationId);
            return null;
        };
    }
}
