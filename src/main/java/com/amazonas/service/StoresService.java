package com.amazonas.service;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.permissions.proxies.StoreProxy;
import com.amazonas.business.stores.search.GlobalSearchRequest;
import com.amazonas.business.stores.search.SearchRequest;
import com.amazonas.business.stores.storePositions.StorePosition;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.exceptions.AuthenticationFailedException;
import com.amazonas.exceptions.NoPermissionException;
import com.amazonas.exceptions.StoreException;
import com.amazonas.service.requests.*;
import com.amazonas.service.requests.store.*;
import com.amazonas.utils.JsonUtils;
import com.amazonas.utils.Response;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
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
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String searchProductsInStore(String json){
        Request request = Request.from(json);
        try {
            SearchInStoreRequest toSearch = JsonUtils.deserialize(request.payload(), SearchInStoreRequest.class);
            return JsonUtils.serialize(proxy.searchProductsInStore(toSearch.storeId(), toSearch.searchRequest(), request.userId(), request.token()));
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addStore(String json) {
        Request request = Request.from(json);
        try {
            StoreCreationRequest toAdd = JsonUtils.deserialize(request.payload(), StoreCreationRequest.class);
            proxy.addStore(toAdd.ownerId(), toAdd.storeName(), toAdd.description(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException  e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String openStore(String json) {
        Request request = Request.from(json);
        try {
            boolean result = proxy.openStore(request.payload(), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String closeStore(String json) {
        Request request = Request.from(json);
        try {
            boolean result = proxy.closeStore(request.payload(), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toAdd = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.addProduct(toAdd.storeId(), toAdd.product(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String updateProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toUpdate = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.updateProduct(toUpdate.storeId(), toUpdate.product(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removeProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toRemove = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.removeProduct(toRemove.storeId(), toRemove.product().productId(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String disableProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toDisable = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.disableProduct(toDisable.storeId(), toDisable.product().productId(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String enableProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toEnable = JsonUtils.deserialize(request.payload(), ProductRequest.class);
            proxy.enableProduct(toEnable.storeId(), toEnable.product().productId(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addOwner(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toAdd = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            proxy.addOwner(toAdd.sourceActor(), toAdd.storeId(), toAdd.targetActor(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addManager(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toAdd = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            proxy.addManager(toAdd.sourceActor(), toAdd.storeId(), toAdd.targetActor(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removeOwner(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toRemove = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            proxy.removeOwner(toRemove.sourceActor(), toRemove.storeId(), toRemove.targetActor(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removeManager(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toRemove = JsonUtils.deserialize(request.payload(), StoreStaffRequest.class);
            proxy.removeManager(toRemove.sourceActor(), toRemove.storeId(), toRemove.targetActor(), request.userId(), request.token());
            return new Response(true).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String addPermissionToManager(String json) {
        Request request = Request.from(json);
        try {
            StorePermissionRequest toAdd = JsonUtils.deserialize(request.payload(), StorePermissionRequest.class);
            boolean result = proxy.addPermissionToManager(toAdd.storeId(), toAdd.targetActor(), StoreActions.valueOf(toAdd.action()), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String removePermissionFromManager(String json) {
        Request request = Request.from(json);
        try {
            StorePermissionRequest toRemove = JsonUtils.deserialize(request.payload(), StorePermissionRequest.class);
            boolean result = proxy.removePermissionFromManager(toRemove.storeId(), toRemove.targetActor(), StoreActions.valueOf(toRemove.action()), request.userId(), request.token());
            return new Response(result).toJson();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String getStoreRolesInformation(String json){
        Request request = Request.from(json);
        try {
            String storeId = JsonUtils.deserialize(request.payload(), String.class);
            List<StorePosition> result = proxy.getStoreRolesInformation(storeId, request.userId(), request.token());
            return new Response(true, result).toJson();
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }

    public String getStoreTransactionHistory(String json){
        Request request = Request.from(json);
        try {
            String storeId = JsonUtils.deserialize(request.payload(), String.class);
            List<Transaction> result = proxy.getStoreTransactionHistory(storeId, request.userId(), request.token());
            return new Response(true, result).toJson();
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getErrorResponse(e).toJson();
        }
    }
}
