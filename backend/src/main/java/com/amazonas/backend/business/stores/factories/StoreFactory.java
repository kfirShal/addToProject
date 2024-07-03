package com.amazonas.backend.business.stores.factories;

import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.stores.Store;
import com.amazonas.backend.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.backend.business.stores.reservations.ReservationFactory;
import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.backend.repository.ProductRepository;
import com.amazonas.backend.repository.TransactionRepository;
import com.amazonas.common.utils.Rating;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("storeFactory")
public class StoreFactory {

    private final ReservationFactory reservationFactory;
    private final PendingReservationMonitor pendingReservationMonitor;
    private final PermissionsController permissionsController;
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    public StoreFactory(ReservationFactory reservationFactory,
                        PendingReservationMonitor pendingReservationMonitor,
                        PermissionsController permissionsController, TransactionRepository transactionRepository, ProductRepository productRepository) {
        this.reservationFactory = reservationFactory;
        this.pendingReservationMonitor = pendingReservationMonitor;
        this.permissionsController = permissionsController;
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    public Store get(String founderUserId, String storeName, String description){
        return new Store(UUID.randomUUID().toString(),
                storeName,
                description,
                Rating.NOT_RATED,
                new ProductInventory(productRepository),
                new AppointmentSystem(founderUserId),
                reservationFactory,
                pendingReservationMonitor,
                permissionsController,
                transactionRepository);
    }

}
