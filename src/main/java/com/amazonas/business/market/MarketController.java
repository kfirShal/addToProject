package com.amazonas.business.market;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.userProfiles.User;

import java.util.List;

public interface MarketController {
    List<Product> searchProducts(GlobalSearchRequest request);

    void getShoppingCartDetails(User user);

    void makePurchase(User user);
}
