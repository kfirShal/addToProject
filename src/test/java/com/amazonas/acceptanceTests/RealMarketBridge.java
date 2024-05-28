package com.amazonas.acceptanceTests;

import com.amazonas.business.market.GlobalSearchRequest;
import com.amazonas.business.userProfiles.User;

public class RealMarketBridge implements MarketBridge{
    @Override
    public boolean searchProduct(GlobalSearchRequest request) {
        return false;
    }

    @Override
    public boolean makePurchase(User user, String token) {
        return false;
    }

    @Override
    public boolean start() {
        return false;
    }

    @Override
    public boolean shutdown() {
        return false;
    }

    @Override
    public boolean restart() {
        return false;
    }
}
