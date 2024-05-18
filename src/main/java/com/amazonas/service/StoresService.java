package com.amazonas.service;

import com.amazonas.business.stores.StoresController;
import org.springframework.stereotype.Component;

@Component
public class StoresService {

    private final StoresController controller;

    public StoresService(StoresController storeProxy) {
        this.controller = storeProxy;
    }
}
