package com.amazonas.business.shipping;

import com.amazonas.service.requests.ShipmentRequest;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class ShippingServiceController {

    private final Map<String, ShippingService> activeShippingServices;
    private final Map<String, ShippingService> disabledShippingServices;

    private final ReadWriteLock lock;

    public ShippingServiceController() {
        activeShippingServices = new HashMap<>();
        disabledShippingServices = new HashMap<>();
        lock = new ReadWriteLock();
    }

    public boolean sendShipment(ShipmentRequest request) {
        try {
            lock.acquireRead();
            if(!activeShippingServices.containsKey(request.serviceId())){
                return false;
            }
            return activeShippingServices.get(request.serviceId()).ship(request.transaction());
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
}
