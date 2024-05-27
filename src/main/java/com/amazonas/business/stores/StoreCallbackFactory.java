package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.utils.Pair;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

@Component("storeCallbackFactory")
public class StoreCallbackFactory {

    private final StoresControllerImpl storesController;

    public StoreCallbackFactory(StoresControllerImpl storesController) {
        this.storesController = storesController;
    }

    public Function<List<Pair<Product,Integer>>,Integer> calculatePrice(String storeId){
        return products -> storesController.getStore(storeId).calculatePrice(products);
    }

    public Function<String,Integer> availableCount(String storeId){
        return productId -> storesController.getStore(storeId).availableCount(productId);
    }

    public Function<List<Pair<Product,Integer>>,Reservation> makeReservation(String storeId, String userId){
        return products -> storesController.getStore(storeId).reserveProducts(userId,products);
    }

    public Function<String,Void> cancelReservation(String storeId){
        return userId -> {
            storesController.getStore(storeId).cancelReservation(userId);
            return null;
        };
    }

}
