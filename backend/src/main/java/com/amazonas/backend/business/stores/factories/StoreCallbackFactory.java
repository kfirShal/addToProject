package com.amazonas.backend.business.stores.factories;

import com.amazonas.backend.business.stores.reservations.Reservation;
import com.amazonas.backend.repository.StoreRepository;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.function.Function;

@Component("storeCallbackFactory")
public class StoreCallbackFactory {


    private final StoreRepository storeRepository;

    public StoreCallbackFactory(StoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

    public Function<Map<String,Integer>, Double> calculatePrice(String storeId){
        return products -> storeRepository.getStore(storeId).calculatePrice(products);
    }

    public Function<String,Integer> availableCount(String storeId){
        return productId -> storeRepository.getStore(storeId).availableCount(productId);
    }

    public Function<Map<String,Integer>, Reservation> makeReservation(String storeId, String userId){
        return products -> storeRepository.getStore(storeId).reserveProducts(products,userId);
    }

    public Function<Reservation,Boolean> cancelReservation(String storeId){
        return reservation ->storeRepository.getStore(storeId).cancelReservation(reservation);
    }

}
