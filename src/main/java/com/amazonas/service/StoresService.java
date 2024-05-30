package com.amazonas.service;

import com.amazonas.business.stores.Store;
import com.amazonas.business.stores.StoresController;
import com.amazonas.exceptions.StoreException;
import com.amazonas.service.requests.StoreCreationRequest;
import com.amazonas.utils.JsonUtils;
import com.amazonas.utils.Response;
import org.springframework.stereotype.Component;

@Component
public class StoresService {

    private final StoresController controller;

    public String addStore(String storeJson){
        StoreCreationRequest toAdd = JsonUtils.deserialize(storeJson, StoreCreationRequest.class);
        try {
            controller.addStore(toAdd.ownerId(), toAdd.storeName(), toAdd.description());
            return new Response(true).toJson();
        }
        catch (StoreException e){
            return Response.getErrorResponse(e).toJson();
        }

    }
    public StoresService(StoresController storeProxy) {
        this.controller = storeProxy;
    }
}
