package com.amazonas.service;

import com.amazonas.business.permissions.proxies.MarketProxy;
import org.springframework.stereotype.Component;

@Component
public class MarketService {

    private final MarketProxy proxy;

    public MarketService(MarketProxy marketProxy) {
        proxy = marketProxy;
    }

}
