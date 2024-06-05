package com.amazonas.service;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.permissions.actions.StoreActions;
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

    public StoresService(StoresController storeProxy) {
        this.controller = storeProxy;
    }

    //TODO: USE PROXY

    public String addStore(String storeJson) {
        StoreCreationRequest toAdd = JsonUtils.deserialize(storeJson, StoreCreationRequest.class);
        try {
            controller.addStore(toAdd.ownerId(), toAdd.storeName(), toAdd.description());
            return new Response(true).toJson();
        } catch (StoreException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String openStore(String storeId) {
        return new Response(controller.openStore(storeId)).toJson();
    }

    public String closeStore(String storeId) {
        return new Response(controller.closeStore(storeId)).toJson();
    }

    public String addProduct(String storeId, String toAddJson) {
        try {
            Product toAdd = JsonUtils.deserialize(toAddJson, Product.class);
            controller.addProduct(storeId, toAdd);
            return new Response(true).toJson();
        } catch (StoreException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String updateProduct(String storeId, String toUpdateJson) {
        try {
            Product toUpdate = JsonUtils.deserialize(toUpdateJson, Product.class);
            controller.updateProduct(storeId, toUpdate);
            return new Response(true).toJson();
        } catch (StoreException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removeProduct(String storeId, String toRemoveJson) {
        try {
            controller.removeProduct(storeId, toRemoveJson);
            return new Response(true).toJson();
        } catch (StoreException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String disableProduct(String storeId, String productId) {
        controller.disableProduct(storeId, productId);
        return new Response(true).toJson();
    }

    public String enableProduct(String storeId, String productId) {
        controller.enableProduct(storeId, productId);
        return new Response(true).toJson();
    }

    public String addOwner(String username, String storeId, String logged) {
        controller.addOwner(username, storeId, logged);
        return new Response(true).toJson();
    }

    public String addManager(String logged, String storeId, String username) {
        controller.addManager(logged, storeId, username);
        return new Response(true).toJson();
    }

    public String removeOwner(String username, String storeId, String logged) {
        controller.removeOwner(username, storeId, logged);
        return new Response(true).toJson();
    }

    public String removeManager(String logged, String storeId, String username) {
        controller.removeManager(logged, storeId, username);
        return new Response(true).toJson();
    }

    public String setReservationTimeoutSeconds(String storeId, long time) {
        controller.setReservationTimeoutSeconds(storeId, time);
        return new Response(true).toJson();
    }

    public String addPermissionToManager(String storeId, String managerId, StoreActions actions) {
        try {
            boolean result = controller.addPermissionToManager(storeId, managerId, actions);
            return new Response(result).toJson();
        } catch (StoreException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removePermissionFromManager(String storeId, String managerId, StoreActions actions) {
        try {
            boolean result = controller.removePermissionFromManager(storeId, managerId, actions);
            return new Response(result).toJson();
        } catch (StoreException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }
}
