package com.amazonas.business.payment;

import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
public class PaymentServiceController {

    private final Map<String, PaymentService> activePaymentServices;
    private final Map<String, PaymentService> disabledPaymentServices;
    private final Map<String, PaymentMethod> activePaymentMethods;
    private final Map<String, PaymentMethod> disabledPaymentMethods;

    private final ReadWriteLock paymentServiceLock;
    private final ReadWriteLock paymentMethodsLock;

    //TODO: implement the payment methods management

    public PaymentServiceController() {
        activePaymentServices = new HashMap<>();
        disabledPaymentServices = new HashMap<>();
        activePaymentMethods = new HashMap<>();
        disabledPaymentMethods = new HashMap<>();
        paymentServiceLock = new ReadWriteLock();
        paymentMethodsLock = new ReadWriteLock();
    }

    public boolean processPayment(PaymentRequest request) {
        try {
            paymentServiceLock.acquireRead();
            if(!activePaymentServices.containsKey(request.serviceId())){
                return false;
            }
            return activePaymentServices.get(request.serviceId()).charge(request.paymentMethod(), request.amount());
        } finally {
            paymentServiceLock.releaseRead();
        }
    }

    // ============================================================================= |
    // =========================== SERVICES MANAGEMENT ============================= |
    // ============================================================================= |

    public void addPaymentService(String serviceId, PaymentService newPaymentService) {
        try {
            paymentServiceLock.acquireWrite();
            activePaymentServices.put(serviceId, newPaymentService);
        } finally {
            paymentServiceLock.releaseWrite();
        }
    }

    public void removePaymentService(String serviceId) {
        try {
            paymentServiceLock.acquireWrite();
            activePaymentServices.remove(serviceId);
        } finally {
            paymentServiceLock.releaseWrite();
        }
    }


    public void updatePaymentService(String serviceId, PaymentService paymentService){
        try{
            paymentServiceLock.acquireWrite();
            if(!activePaymentServices.containsKey(serviceId)){
                return;
            }
            activePaymentServices.put(serviceId, paymentService);
        } finally {
            paymentServiceLock.releaseWrite();
        }
    }

    public void enablePaymentService(String serviceId) {
        paymentServiceLock.acquireWrite();  // block until condition holds
        try {
            if(disabledPaymentServices.containsKey(serviceId)) {
                activePaymentServices.put(serviceId, disabledPaymentServices.remove(serviceId));
            }
        } finally {
            paymentServiceLock.releaseWrite();
        }
    }

    public void enableAllPaymentServices() {
        paymentServiceLock.acquireWrite();  // block until condition holds
        try {
            Set<String> paymentServiceSet = disabledPaymentServices.keySet();
            paymentServiceSet.forEach(service ->
                    activePaymentServices.put(service,disabledPaymentServices.remove(service)));
        } finally {
            paymentServiceLock.releaseWrite();
        }
    }

    public void disablePaymentService(String serviceId) {
        paymentServiceLock.acquireWrite();  // block until condition holds
        try {
            if(activePaymentServices.containsKey(serviceId)) {
                disabledPaymentServices.put(serviceId, activePaymentServices.remove(serviceId));
            }
        } finally {
            paymentServiceLock.releaseWrite();
        }
    }

    public void disableAllPaymentServices() {
        paymentServiceLock.acquireWrite();  // block until condition holds
        try {
            Set<String> paymentServiceSet = activePaymentServices.keySet();
            paymentServiceSet.forEach(service ->
                    disabledPaymentServices.put(service, activePaymentServices.remove(service)));
        } finally {
            paymentServiceLock.releaseWrite();
        }
    }

    // ============================================================================= |
    // ============================== PAYMENT METHODS ============================== |
    // ============================================================================= |

    public void addPaymentMethod(PaymentMethod newPaymentMethod) {
    }

    public void removePaymentMethod(PaymentMethod oldPaymentMethod) {
    }

    public void updatePaymentMethod(PaymentMethod paymentMethod) {

    }

    public void enablePaymentMethod(PaymentMethod paymentMethod) {

    }

    public void enableAllPaymentMethods() {

    }

    public void disablePaymentMethod(PaymentMethod paymentMethod) {

    }

    public void disableAllPaymentMethods() {

    }

    public boolean areAllPaymentServicesEnabled() {
        try {
            paymentServiceLock.acquireRead();
            return disabledPaymentServices.isEmpty();
        } finally {
            paymentServiceLock.releaseRead();
        }
    }

    public boolean areAllPaymentMethodsEnabled() {
        try {
            paymentMethodsLock.acquireRead();
            return disabledPaymentMethods.isEmpty();
        } finally {
            paymentMethodsLock.releaseRead();
        }
    }

}