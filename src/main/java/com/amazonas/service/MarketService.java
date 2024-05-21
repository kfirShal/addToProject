package com.amazonas.service;

import com.amazonas.business.market.MarketFacade;
import org.springframework.stereotype.Component;

@Component
public class MarketService {

    private final MarketFacade controller;

    public MarketService(MarketFacade marketProxy) {
        this.controller = marketProxy;
    }

}
