package com.amazonas.service;

import com.amazonas.business.market.MarketFacade;
import com.amazonas.business.transactions.TransactionsController;
import org.springframework.stereotype.Component;

@Component
public class MarketService {

    private final MarketFacade controller;
    private final TransactionsController transactionsController;

    public MarketService(MarketFacade marketProxy, TransactionsController transactionsController) {
        this.controller = marketProxy;
        this.transactionsController = transactionsController;
    }

}
