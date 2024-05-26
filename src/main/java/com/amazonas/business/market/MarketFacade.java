package com.amazonas.business.market;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;

import java.util.List;

public interface MarketFacade {

    List<Product> searchProducts(GlobalSearchRequest request) throws NoPermissionException, AuthenticationFailedException;

    void makePurchase(String userId, String token) throws NoPermissionException, AuthenticationFailedException;
    
    void addShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException;
    
    void removeShippingService(ShippingService shippingService)  throws NoPermissionException, AuthenticationFailedException;
    
    void updateShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException;
    
    void enableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException;
    
    void disableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException;
    
    void addPaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException;
    
    void removePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException;
    
    void updatePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException;

    void enablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException;
    
    void disablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException;
    
    void addPaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException;
    
    void removePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException;
    
    void updatePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException;
    
    void enablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException;
    
    void disablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException;

    void start() throws NoPermissionException, AuthenticationFailedException;
    
    void shutdown() throws NoPermissionException, AuthenticationFailedException;
    
    void restart() throws NoPermissionException, AuthenticationFailedException;
}
