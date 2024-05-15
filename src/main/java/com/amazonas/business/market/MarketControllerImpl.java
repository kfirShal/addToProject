package com.amazonas.business.market;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.userProfiles.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("marketController")
public class MarketControllerImpl implements MarketController {

    @Override
    public List<Product> searchProducts(GlobalSearchRequest request) {
        return null;
    }

    @Override
    public void getShoppingCartDetails(User user, String token){
        // this method will return something, for now it's void
    }

    @Override
    public void makePurchase(User user, String token){
        // this method will return something, for now it's void
    }

}
