package com.amazonas.business.stores;

import com.amazonas.business.inventory.ProductInventory;
import org.springframework.stereotype.Component;

@Component("storesFactory")
public class StoresFactory {

    private final ReservationFactory reservationFactory;
    private final ReservationMonitor reservationMonitor;

    public StoresFactory(ReservationFactory reservationFactory, ReservationMonitor reservationMonitor) {
        this.reservationFactory = reservationFactory;
        this.reservationMonitor = reservationMonitor;
    }

    public Store get(String ownerUserId, String storeId, String description, Rating rating){
        return new Store(ownerUserId,
                storeId,
                description,
                rating,
                new ProductInventory(),
                reservationFactory,reservationMonitor);
    }
}
