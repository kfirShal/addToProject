package com.amazonas.business.stores.factories;

import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.utils.Rating;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.reservations.ReservationFactory;
import com.amazonas.business.stores.reservations.ReservationMonitor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("storeFactory")
public class StoreFactory {

    private final ReservationFactory reservationFactory;
    private final ReservationMonitor reservationMonitor;
    private final PermissionsController permissionsController;

    public StoreFactory(ReservationFactory reservationFactory, ReservationMonitor reservationMonitor, PermissionsController permissionsController) {
        this.reservationFactory = reservationFactory;
        this.reservationMonitor = reservationMonitor;
        this.permissionsController = permissionsController;
    }

    public Store get(String ownerUserId, String description){
        return new Store(ownerUserId,
                UUID.randomUUID().toString(),
                description,
                Rating.NOT_RATED,
                new ProductInventory(),
                reservationFactory,reservationMonitor,
                permissionsController);
    }
}
