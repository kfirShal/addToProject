package com.amazonas.service;

import com.amazonas.business.market.MarketController;
import org.springframework.stereotype.Component;

@Component
public class MarketService {

    private final MarketController controller;

    public MarketService(MarketController marketProxy) {
        this.controller = marketProxy;
    }

}
