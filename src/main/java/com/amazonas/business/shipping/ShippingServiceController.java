package com.amazonas.business.shipping;

import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ShippingServiceController {

    private final Map<String, ShippingService> shippingServices;
    private final ReadWriteLock lock;

    public ShippingServiceController() {
        shippingServices = new HashMap<>();
        lock = new ReadWriteLock();
    }

    public void addShippingService(String serviceId, ShippingService newShippingService) {
        try {
            lock.acquireWrite();
            shippingServices.put(serviceId, newShippingService);
        } finally {
            lock.releaseWrite();
        }
    }

    public void removeShippingService(String serviceId) {
        try {
            lock.acquireWrite();
            shippingServices.remove(serviceId);
        } finally {
            lock.releaseWrite();
        }
    }


    public void updateShippingService(String serviceId, ShippingService shippingService){
        try{
            lock.acquireWrite();
            if(!shippingServices.containsKey(serviceId)){
                return;
            }
            shippingServices.put(serviceId, shippingService);
        } finally {
            lock.releaseWrite();
        }
    }
}
