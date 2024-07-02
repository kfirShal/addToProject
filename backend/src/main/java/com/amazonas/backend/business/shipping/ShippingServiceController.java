package com.amazonas.backend.business.shipping;

import com.amazonas.backend.business.stores.Store;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.backend.repository.StoreRepository;
import com.amazonas.backend.repository.TransactionRepository;
import com.amazonas.common.utils.ReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ShippingServiceController {

    private static final Logger log = LoggerFactory.getLogger(ShippingServiceController.class);

    private final Map<String, ShippingService> activeShippingServices;
    private final Map<String, ShippingService> disabledShippingServices;

    private final ReadWriteLock lock;
    private final StoreRepository storeRepository;
    private final TransactionRepository transactionRepository;

    public ShippingServiceController(StoreRepository storeRepository, TransactionRepository transactionRepository) {
        activeShippingServices = new HashMap<>();
        disabledShippingServices = new HashMap<>();
        lock = new ReadWriteLock();
        this.storeRepository = storeRepository;
        this.transactionRepository = transactionRepository;
    }

    public boolean sendShipment(String transactionId, String serviceId) {
        try {
            lock.acquireRead();
            if(!activeShippingServices.containsKey(serviceId)){
                return false;
            }
            Transaction transaction = transactionRepository.getTransactionById(transactionId);
            boolean shipped = activeShippingServices.get(serviceId).ship(transaction);
            if(shipped){
                Store store = storeRepository.getStore(transaction.storeId());
                try {
                    store.setOrderShipped(transactionId);
                } catch (StoreException e) {
                    log.error("Failed to set order as shipped in store", e);
                    shipped = false;
                }
            }
            return shipped;
        } finally {
            lock.releaseRead();
        }
    }

    public void addShippingService(String serviceId, ShippingService newShippingService) {
        try {
            lock.acquireWrite();
            activeShippingServices.put(serviceId, newShippingService);
        } finally {
            lock.releaseWrite();
        }
    }

    public void removeShippingService(String serviceId) {
        try {
            lock.acquireWrite();
            activeShippingServices.remove(serviceId);
        } finally {
            lock.releaseWrite();
        }
    }

    public void updateShippingService(String serviceId, ShippingService shippingService){
        try{
            lock.acquireWrite();
            if(!activeShippingServices.containsKey(serviceId)){
                return;
            }
            activeShippingServices.put(serviceId, shippingService);
        } finally {
            lock.releaseWrite();
        }
    }

    public void enableShippingService(String serviceId) {
        lock.acquireWrite();  // block until condition holds
        try {
            if(disabledShippingServices.containsKey(serviceId)) {
                activeShippingServices.put(serviceId, disabledShippingServices.remove(serviceId));
            }
        } finally {
            lock.releaseWrite();
        }
    }

    public void enableAllShippingServices() {
        lock.acquireWrite();  // block until condition holds
        try {
            Set<String> shippingServiceSet = disabledShippingServices.keySet();
            shippingServiceSet.forEach(service ->
                    activeShippingServices.put(service,disabledShippingServices.remove(service)));
        } finally {
            lock.releaseWrite();
        }
    }

    public void disableShippingService(String serviceId) {
        lock.acquireWrite();  // block until condition holds
        try {
            if(activeShippingServices.containsKey(serviceId)) {
                disabledShippingServices.put(serviceId, activeShippingServices.remove(serviceId));
            }
        } finally {
            lock.releaseWrite();
        }
    }

    public void disableAllShippingServices() {
        lock.acquireWrite();  // block until condition holds
        try {
            Set<String> shippingServiceSet = activeShippingServices.keySet();
            shippingServiceSet.forEach(service ->
                    disabledShippingServices.put(service, activeShippingServices.remove(service)));
        } finally {
            lock.releaseWrite();
        }
    }

    public boolean isShippingServiceEnabled(String serviceId) {
        try {
            lock.acquireRead();
            return activeShippingServices.containsKey(serviceId);
        } finally {
            lock.releaseRead();
        }
    }

    public boolean areAllShippingServicesEnabled() {
        try {
            lock.acquireRead();
            return disabledShippingServices.isEmpty();
        } finally {
            lock.releaseRead();
        }
    }
}
