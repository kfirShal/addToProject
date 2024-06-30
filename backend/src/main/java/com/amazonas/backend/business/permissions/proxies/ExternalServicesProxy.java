package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.payment.PaymentService;
import com.amazonas.backend.business.payment.PaymentServiceController;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.permissions.actions.MarketActions;
import com.amazonas.backend.business.permissions.actions.StoreActions;
import com.amazonas.backend.business.shipping.ShippingService;
import com.amazonas.backend.business.shipping.ShippingServiceController;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

@Component("externalServicesProxy")
public class ExternalServicesProxy extends ControllerProxy {

    private final ShippingServiceController shippingServiceController;
    private final PaymentServiceController paymentServiceController;

    public ExternalServicesProxy(ShippingServiceController shippingServiceController, PaymentServiceController paymentServiceController, PermissionsController permissionsController, AuthenticationController authenticationController) {
        super(permissionsController, authenticationController);
        this.shippingServiceController = shippingServiceController;
        this.paymentServiceController = paymentServiceController;
    }

    public boolean sendShipment(String transactionId, String serviceId, String storeId, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, storeId, StoreActions.SEND_SHIPMENT);
        return shippingServiceController.sendShipment(transactionId, serviceId);
    }

    public void addShippingService(String serviceId, ShippingService shippingService, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.ADD_SHIPPING_SERVICE);
        shippingServiceController.addShippingService(serviceId, shippingService);
    }

    public void removeShippingService(String serviceId, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.REMOVE_SHIPPING_SERVICE);
        shippingServiceController.removeShippingService(serviceId);
    }

    public void updateShippingService(String serviceId, ShippingService shippingService, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.UPDATE_SHIPPING_SERVICE);
        shippingServiceController.updateShippingService(serviceId, shippingService);
    }

    public void addPaymentService(String serviceId, PaymentService paymentService, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.ADD_PAYMENT_SERVICE);
        paymentServiceController.addPaymentService(serviceId, paymentService);
    }

    public void removePaymentService(String serviceId, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.REMOVE_PAYMENT_SERVICE);
        paymentServiceController.removePaymentService(serviceId);
    }

    public void updatePaymentService(String serviceId, PaymentService paymentService, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.UPDATE_PAYMENT_SERVICE);
        paymentServiceController.updatePaymentService(serviceId, paymentService);
    }
}
