package com.amazonas.backend.business.stores.factories;

import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.backend.business.stores.reservations.ReservationFactory;
import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.backend.repository.TransactionRepository;
import com.amazonas.common.utils.Rating;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
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
