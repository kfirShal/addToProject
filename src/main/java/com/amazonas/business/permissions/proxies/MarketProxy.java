package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.market.GlobalSearchRequest;
import com.amazonas.business.market.MarketActions;
import com.amazonas.business.market.MarketController;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.stores.SearchRequest;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.User;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("marketProxy")
public class MarketProxy extends ControllerProxy implements MarketController {

    private final MarketController real;

    public MarketProxy(MarketController marketController, PermissionsController perm, AuthenticationController auth) {
        super(perm,auth);
        this.real = marketController;
    }

    @Override
    public List<Product> searchProducts(GlobalSearchRequest request) {
        return real.searchProducts(request);
    }


    @Override
    public ShoppingCart getShoppingCartDetails(User user, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(user.getUserId() ,token);
        if(! perm.checkPermission(user.getUserId(), MarketActions.VIEW_SHOPPING_CART)) {
            throw new NoPermissionException("User does not have permission to view shopping cart");
        }

        return real.getShoppingCartDetails(user, token);
    }

    @Override
    public void makePurchase(User user, String token, PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(user.getUserId() ,token);
        if(! perm.checkPermission(user.getUserId(), MarketActions.MAKE_PURCHASE)) {
            throw new NoPermissionException("User does not have permission to make a purchase");
        }

        real.makePurchase(user, token, paymentMethod);
    }
}
