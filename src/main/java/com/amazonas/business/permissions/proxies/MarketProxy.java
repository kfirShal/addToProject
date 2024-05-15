package com.amazonas.business.permissions.proxies;

import com.amazonas.business.authentication.AuthenticationController;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.market.GlobalSearchRequest;
import com.amazonas.business.market.MarketController;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.userProfiles.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
    public void getShoppingCartDetails(User user, String token) {
        validateToken(user.getUserId() ,token);
        real.getShoppingCartDetails(user, null);
    }

    @Override
    public void makePurchase(User user, String token) {
        validateToken(user.getUserId() ,token);
        real.makePurchase(user, null);
    }
}
