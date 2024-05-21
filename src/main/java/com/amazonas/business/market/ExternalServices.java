package com.amazonas.business.market;

import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.shipping.ShippingService;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

public class ExternalServices {
    private Map<PaymentMethod, Boolean> paymentMethods;
    private Map<PaymentService, Boolean> paymentServices;
    private Map<ShippingService, Boolean> shippingServices;
    private final ReentrantLock paymentMethodsLock;
    private final ReentrantLock paymentServicesLock;
    private final ReentrantLock shippingServicesLock;
    private ExternalServices() {
        paymentMethods = new HashMap<>();
        paymentServices = new HashMap<>();
        shippingServices = new HashMap<>();
        paymentMethodsLock = new ReentrantLock(true);
        paymentServicesLock = new ReentrantLock(true);
        shippingServicesLock = new ReentrantLock(true);
    }

    /**
     * SingletonHolder is loaded on the first execution of Singleton.getInstance()
     * or the first access to SingletonHolder.INSTANCE, not before.
     */
    private static class SingletonHolder {
        private final static ExternalServices INSTANCE = new ExternalServices();
    }

    public static ExternalServices getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public void addPaymentService(PaymentService newPaymentService) {
        paymentServicesLock.lock();  // block until condition holds
        try {
            paymentServices.put(newPaymentService, true);
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void removePaymentService(PaymentService oldPaymentService) {
        paymentServicesLock.lock();  // block until condition holds
        try {
            paymentServices.remove(oldPaymentService);
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void addPaymentMethod(PaymentMethod newPaymentMethod) {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            paymentMethods.put(newPaymentMethod, true);
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void removePaymentMethod(PaymentMethod oldPaymentMethod) {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            paymentMethods.remove(oldPaymentMethod);
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void addShippingService(ShippingService newShoppingService) {
        shippingServicesLock.lock();  // block until condition holds
        try {
            shippingServices.put(newShoppingService, true);
        } finally {
            shippingServicesLock.unlock();
        }
    }

    public void removeShippingService(ShippingService oldShoppingService) {
        shippingServicesLock.lock();  // block until condition holds
        try {
            shippingServices.remove(oldShoppingService);
        } finally {
            shippingServicesLock.unlock();
        }
    }

    public void enablePaymentService(PaymentService paymentService) {
        paymentServicesLock.lock();  // block until condition holds
        try {
            if(paymentServices.containsKey(paymentService)) {
                paymentServices.put(paymentService, true);
            }
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void enableAllPaymentServices() {
        paymentServicesLock.lock();  // block until condition holds
        try {
            Set<PaymentService> paymentServiceSet = paymentServices.keySet();
            for (PaymentService PS: paymentServiceSet) {
                paymentServices.put(PS, true);
            }
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void disablePaymentService(PaymentService paymentService) {
        paymentServicesLock.lock();  // block until condition holds
        try {
            if(paymentServices.containsKey(paymentService)) {
                paymentServices.put(paymentService, false);
            }
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void disableAllPaymentServices() {
        paymentServicesLock.lock();  // block until condition holds
        try {
            Set<PaymentService> paymentServiceSet = paymentServices.keySet();
            for (PaymentService PS: paymentServiceSet) {
                paymentServices.put(PS, false);
            }
        } finally {
            paymentServicesLock.unlock();
        }
    }

    public void enablePaymentMethod(PaymentMethod paymentMethod) {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            if(paymentMethods.containsKey(paymentMethod)) {
                paymentMethods.put(paymentMethod, true);
            }
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void enableAllPaymentMethods() {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            Set<PaymentMethod> paymentMethodSet = paymentMethods.keySet();
            for (PaymentMethod PM: paymentMethodSet) {
                paymentMethods.put(PM, true);
            }
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void disablePaymentMethod(PaymentMethod paymentMethod) {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            if(paymentMethods.containsKey(paymentMethod)) {
                paymentMethods.put(paymentMethod, false);
            }
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void disableAllPaymentMethods() {
        paymentMethodsLock.lock();  // block until condition holds
        try {
            Set<PaymentMethod> paymentMethodSet = paymentMethods.keySet();
            for (PaymentMethod PM: paymentMethodSet) {
                paymentMethods.put(PM, false);
            }
        } finally {
            paymentMethodsLock.unlock();
        }
    }

    public void enableShippingService(ShippingService shippingService) {
        shippingServicesLock.lock();  // block until condition holds
        try {
            if(shippingServices.containsKey(shippingService)) {
                shippingServices.put(shippingService, true);
            }
        } finally {
            shippingServicesLock.unlock();
        }
    }

    public void enableAllShippingServices() {
        shippingServicesLock.lock();  // block until condition holds
        try {
            Set<ShippingService> shippingServiceSet = shippingServices.keySet();
            for (ShippingService SS: shippingServiceSet) {
                shippingServices.put(SS, true);
            }
        } finally {
            shippingServicesLock.unlock();
        }
    }

    public void disableShippingService(ShippingService shippingService) {
        shippingServicesLock.lock();  // block until condition holds
        try {
            if(shippingServices.containsKey(shippingService)) {
                shippingServices.put(shippingService, false);
            }
        } finally {
            shippingServicesLock.unlock();
        }
    }

    public void disableAllShippingServices() {
        shippingServicesLock.lock();  // block until condition holds
        try {
            Set<ShippingService> shippingServiceSet = shippingServices.keySet();
            for (ShippingService SS: shippingServiceSet) {
                shippingServices.put(SS, false);
            }
        } finally {
            shippingServicesLock.unlock();
        }
    }
}
