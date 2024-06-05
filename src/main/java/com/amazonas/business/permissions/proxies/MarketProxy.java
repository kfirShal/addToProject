package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.market.MarketInitializer;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

@Component("marketProxy")
public class MarketProxy extends ControllerProxy {

    private final MarketInitializer real;

    public MarketProxy(MarketInitializer marketInitializer, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = marketInitializer;
    }
    
    public void addShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void removeShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void updateShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void enableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void disableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void addPaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void removePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void updatePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void enablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void disablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void addPaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void removePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void updatePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void enablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void disablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void start() throws NoPermissionException, AuthenticationFailedException {
        
    }

    
    public void shutdown() throws NoPermissionException, AuthenticationFailedException {

    }

    
    public void restart() throws NoPermissionException, AuthenticationFailedException {

    }
}
