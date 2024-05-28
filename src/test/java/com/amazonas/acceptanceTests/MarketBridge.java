package com.amazonas.acceptanceTests;

import com.amazonas.business.market.GlobalSearchRequest;
import com.amazonas.business.userProfiles.User;

public interface MarketBridge {
    boolean searchProduct(GlobalSearchRequest request);
    boolean makePurchase(User user, String token);
    boolean start();
    boolean shutdown();
    boolean restart();

}
