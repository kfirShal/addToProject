package com.amazonas.backend.service;

import com.amazonas.backend.business.permissions.actions.StoreActions;
import com.amazonas.backend.business.permissions.proxies.StoreProxy;
import com.amazonas.backend.business.stores.storePositions.StorePosition;
import com.amazonas.backend.business.transactions.Transaction;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.store.*;
import com.amazonas.common.utils.JsonUtils;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component("storesService")
public class StoresService {

    private final StoreProxy proxy;

    public StoresService(StoreProxy storeProxy) {
        this.proxy = storeProxy;
    }

    public String searchProductsGlobally(String json) {
        Request request = Request.from(json);
        try {
            GlobalSearchRequest toSearch = JsonUtils.deserialize(request.payload(), GlobalSearchRequest.class);
            return JsonUtils.serialize(proxy.searchProductsGlobally(toSearch, request.userId(), request.token()));
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String searchProductsInStore(String json){
        Request request = Request.from(json);
        try {
            SearchInStoreRequest toSearch = JsonUtils.deserialize(request.payload(), SearchInStoreRequest.class);
            return JsonUtils.serialize(proxy.searchProductsInStore(toSearch.storeId(), toSearch.searchRequest(), request.userId(), request.token()));
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addStore(String json) {
        Request request = Request.from(json);
        try {
            StoreCreationRequest toAdd = JsonUtils.deserialize(request.payload(), StoreCreationRequest.class);
            proxy.addStore(toAdd.ownerId(), toAdd.storeName(), toAdd.description(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException  e) {
            return Response.getError(e);
        }
    }

    public String openStore(String json) {
        Request request = Request.from(json);
        try {
            boolean result = proxy.openStore(request.payload(), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String closeStore(String json) {
        Request request = Request.from(json);
        try {
            boolean result = proxy.closeStore(request.payload(), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toAdd = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.addProduct(toAdd.storeId(), toAdd.product(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String updateProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toUpdate = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.updateProduct(toUpdate.storeId(), toUpdate.product(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removeProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toRemove = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.removeProduct(toRemove.storeId(), toRemove.product().productId(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String disableProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toDisable = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.disableProduct(toDisable.storeId(), toDisable.product().productId(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String enableProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toEnable = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.enableProduct(toEnable.storeId(), toEnable.product().productId(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addOwner(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toAdd = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            proxy.addOwner(toAdd.sourceActor(), toAdd.storeId(), toAdd.targetActor(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addManager(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toAdd = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            proxy.addManager(toAdd.sourceActor(), toAdd.storeId(), toAdd.targetActor(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removeOwner(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toRemove = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            proxy.removeOwner(toRemove.sourceActor(), toRemove.storeId(), toRemove.targetActor(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removeManager(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toRemove = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            proxy.removeManager(toRemove.sourceActor(), toRemove.storeId(), toRemove.targetActor(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addPermissionToManager(String json) {
        Request request = Request.from(json);
        try {
            StorePermissionRequest toAdd = JsonUtils.deserialize(request.payload(), StorePermissionRequest.class);
            boolean result = proxy.addPermissionToManager(toAdd.storeId(), toAdd.targetActor(), StoreActions.valueOf(toAdd.action()), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removePermissionFromManager(String json) {
        Request request = Request.from(json);
        try {
            StorePermissionRequest toRemove = JsonUtils.deserialize(request.payload(), StorePermissionRequest.class);
            boolean result = proxy.removePermissionFromManager(toRemove.storeId(), toRemove.targetActor(), StoreActions.valueOf(toRemove.action()), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String getStoreRolesInformation(String json){
        Request request = Request.from(json);
        try {
            String storeId = JsonUtils.deserialize(request.payload(), String.class);
            List<StorePosition> result = proxy.getStoreRolesInformation(storeId, request.userId(), request.token());
            return Response.getOk(result);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String getStoreTransactionHistory(String json){
        Request request = Request.from(json);
        try {
            String storeId = JsonUtils.deserialize(request.payload(), String.class);
            List<Transaction> result = proxy.getStoreTransactionHistory(storeId, request.userId(), request.token());
            return Response.getOk(result);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }
}
