package com.amazonas.business.permissions.proxies;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.market.GlobalSearchRequest;
import com.amazonas.business.market.MarketController;
import com.amazonas.business.userProfiles.User;

import java.util.List;

public class MarketProxy implements MarketController {
    @Override
    public List<Product> searchProducts(GlobalSearchRequest request) {
        return List.of();
    }

    @Override
    public void getShoppingCartDetails(User user) {

    }

    @Override
    public void makePurchase(User user) {

    }
}
