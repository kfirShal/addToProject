package com.amazonas.service;

import com.amazonas.business.market.MarketFacade;
import com.amazonas.business.permissions.proxies.MarketProxy;
import com.amazonas.business.transactions.TransactionsController;
import org.springframework.stereotype.Component;

@Component
public class MarketService {

    private final MarketProxy proxy;

    public MarketService(MarketProxy marketProxy) {
        proxy = marketProxy;
    }

}
