package com.amazonas.business.payment;

import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentServiceController {

    private final Map<String, PaymentService> paymentServices;
    private final ReadWriteLock lock;

    public PaymentServiceController() {
        paymentServices = new HashMap<>();
        lock = new ReadWriteLock();
    }

    public void addPaymentService(String serviceId, PaymentService newPaymentService) {
        try {
            lock.acquireWrite();
            paymentServices.put(serviceId, newPaymentService);
        } finally {
            lock.releaseWrite();
        }
    }

    public void removePaymentService(String serviceId) {
        try {
            lock.acquireWrite();
            paymentServices.remove(serviceId);
        } finally {
            lock.releaseWrite();
        }
    }

    public void updatePaymentService(String serviceId, PaymentService paymentService){
        try{
            lock.acquireWrite();
            if(!paymentServices.containsKey(serviceId)){
                return;
            }
            paymentServices.put(serviceId, paymentService);
        } finally {
            lock.releaseWrite();
        }
    }
}