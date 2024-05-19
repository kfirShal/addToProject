package com.amazonas.business.market;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.payment.PaymentMethod;
import com.amazonas.business.stores.SearchRequest;
import com.amazonas.business.userProfiles.ShoppingCart;
import com.amazonas.business.userProfiles.User;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;

import java.util.List;

public interface MarketController {

    List<Product> searchProducts(GlobalSearchRequest request);

    ShoppingCart getShoppingCartDetails(User user, String token) throws NoPermissionException, AuthenticationFailedException;

    void makePurchase(User user, String token, PaymentMethod paymentMethod) throws NoPermissionException, AuthenticationFailedException;
}
