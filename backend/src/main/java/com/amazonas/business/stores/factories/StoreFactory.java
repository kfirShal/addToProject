package com.amazonas.business.stores.factories;

import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.business.stores.reservations.ReservationFactory;
import com.amazonas.business.stores.storePositions.AppointmentSystem;
import com.amazonas.common.utils.Rating;
import com.amazonas.repository.TransactionRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("storeFactory")
public class StoreFactory {

    private final ReservationFactory reservationFactory;
    private final PendingReservationMonitor pendingReservationMonitor;
    private final PermissionsController permissionsController;
    private final TransactionRepository transactionRepository;

    public StoreFactory(ReservationFactory reservationFactory,
                        PendingReservationMonitor pendingReservationMonitor,
                        PermissionsController permissionsController, TransactionRepository transactionRepository) {
        this.reservationFactory = reservationFactory;
        this.pendingReservationMonitor = pendingReservationMonitor;
        this.permissionsController = permissionsController;
        this.transactionRepository = transactionRepository;
    }

    public Store get(String founderUserId, String storeName, String description){
        return new Store(UUID.randomUUID().toString(),
                storeName,
                description,
                Rating.NOT_RATED,
                new ProductInventory(),
                new AppointmentSystem(founderUserId),
                reservationFactory,
                pendingReservationMonitor,
                permissionsController,
                transactionRepository);
    }
}
