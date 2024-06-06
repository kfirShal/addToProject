package com.amazonas.service;

import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.permissions.proxies.StoreProxy;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.exceptions.StoreException;
import com.amazonas.service.requests.*;
import com.amazonas.service.requests.store.ProductRequest;
import com.amazonas.service.requests.store.StoreCreationRequest;
import com.amazonas.service.requests.store.StorePermissionRequest;
import com.amazonas.service.requests.store.StoreStaffRequest;
import com.amazonas.utils.JsonUtils;
import com.amazonas.utils.Response;
import org.springframework.stereotype.Component;

@Component
public class StoresService {

    private final StoreProxy controller;

    public StoresService(StoreProxy storeProxy) {
        this.controller = storeProxy;
    }

    public String addStore(String json) {
        Request request = Request.from(json);
        try {
            StoreCreationRequest toAdd = JsonUtils.deserialize(request.payload(), StoreCreationRequest.class);
            controller.addStore(toAdd.ownerId(), toAdd.storeName(), toAdd.description(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException  e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String openStore(String json) {
        Request request = Request.from(json);
        try {
            boolean result = controller.openStore(request.payload(), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String closeStore(String json) {
        Request request = Request.from(json);
        try {
            boolean result = controller.closeStore(request.payload(), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toAdd = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            controller.addProduct(toAdd.storeId(), toAdd.product(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String updateProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toUpdate = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            controller.updateProduct(toUpdate.storeId(), toUpdate.product(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removeProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toRemove = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            controller.removeProduct(toRemove.storeId(), toRemove.product().productId(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String disableProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toDisable = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            controller.disableProduct(toDisable.storeId(), toDisable.product().productId(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String enableProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toEnable = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            controller.enableProduct(toEnable.storeId(), toEnable.product().productId(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addOwner(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toAdd = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            controller.addOwner(toAdd.sourceActor(), toAdd.storeId(), toAdd.targetActor(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addManager(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toAdd = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            controller.addManager(toAdd.sourceActor(), toAdd.storeId(), toAdd.targetActor(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removeOwner(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toRemove = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            controller.removeOwner(toRemove.sourceActor(), toRemove.storeId(), toRemove.targetActor(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removeManager(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toRemove = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            controller.removeManager(toRemove.sourceActor(), toRemove.storeId(), toRemove.targetActor(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addPermissionToManager(String json) {
        Request request = Request.from(json);
        try {
            StorePermissionRequest toAdd = JsonUtils.deserialize(request.payload(), StorePermissionRequest.class);
            boolean result = controller.addPermissionToManager(toAdd.storeId(), toAdd.targetActor(), StoreActions.valueOf(toAdd.action()), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removePermissionFromManager(String json) {
        Request request = Request.from(json);
        try {
            StorePermissionRequest toRemove = JsonUtils.deserialize(request.payload(), StorePermissionRequest.class);
            boolean result = controller.removePermissionFromManager(toRemove.storeId(), toRemove.targetActor(), StoreActions.valueOf(toRemove.action()), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }
}
