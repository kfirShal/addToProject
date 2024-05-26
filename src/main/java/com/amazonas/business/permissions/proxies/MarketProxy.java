package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.market.GlobalSearchRequest;
import com.amazonas.business.market.MarketActions;
import com.amazonas.business.market.MarketFacade;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.payment.PaymentService;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.shipping.ShippingService;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("marketProxy")
public class MarketProxy extends ControllerProxy implements MarketFacade {

    private final MarketFacade real;

    public MarketProxy(MarketFacade marketFacade, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = marketFacade;
    }

    @Override
    public List<Product> searchProducts(GlobalSearchRequest request) throws AuthenticationFailedException, NoPermissionException {
        return real.searchProducts(request);
    }

    @Override
    public void makePurchase(String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId ,token);
        if(! perm.checkPermission(userId, MarketActions.MAKE_PURCHASE)) {
            throw new NoPermissionException("User does not have permission to make a purchase");
        }

        real.makePurchase(userId, token);
    }

    @Override
    public void addShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void removeShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void updateShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void enableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void disableShippingService(ShippingService shippingService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void addPaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void removePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void updatePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void enablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void disablePaymentService(PaymentService paymentService) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void addPaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void removePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void updatePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void enablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void disablePaymentMethod(PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void start() throws NoPermissionException, AuthenticationFailedException {
        
    }

    @Override
    public void shutdown() throws NoPermissionException, AuthenticationFailedException {

    }

    @Override
    public void restart() throws NoPermissionException, AuthenticationFailedException {

    }
}
