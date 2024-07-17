package com.amazonas.backend.service;

import com.amazonas.backend.exceptions.DiscountPolicyException;
import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.common.PurchaseRuleDTO.PurchaseRuleDTO;

import com.amazonas.common.dtos.Product;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.backend.business.permissions.proxies.StoreProxy;
import com.amazonas.common.dtos.StorePosition;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.requests.Request;
import com.amazonas.common.requests.stores.*;
import com.amazonas.common.utils.Response;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;


@Component("storesService")
public class StoresService {

    private final StoreProxy proxy;

    public StoresService(StoreProxy storeProxy) {
        this.proxy = storeProxy;
    }

    public String getStoreDetails(String json) {
        Request request = Request.from(json);
        try {
            String storeId = request.payload();
            return Response.getOk(proxy.getStoreDetails(storeId, request.userId(), request.token()));
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String searchProductsGlobally(String json) {
        Request request = Request.from(json);
        try {
            GlobalSearchRequest toSearch = GlobalSearchRequest.from(request.payload());
            return Response.getOk(proxy.searchProductsGlobally(toSearch, request.userId(), request.token()));
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String searchStoresGlobally(String json) {
        Request request = Request.from(json);
        try {
            StoreSearchRequest toSearch = StoreSearchRequest.from(request.payload());
            return Response.getOk(proxy.searchStoresGlobally(toSearch.storeName(), request.userId(), request.token()));
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String searchProductsInStore(String json){
        Request request = Request.from(json);
        try {
            SearchInStoreRequest toSearch = SearchInStoreRequest.from(request.payload());
            return Response.getOk(proxy.searchProductsInStore(toSearch.storeId(), toSearch.productSearchRequest(), request.userId(), request.token()));
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addStore(String json) {
        Request request = Request.from(json);
        try {
            StoreCreationRequest toAdd = StoreCreationRequest.from(request.payload());
            String storeId = proxy.addStore(toAdd.founderId(), toAdd.storeName(), toAdd.description(), request.userId(), request.token());
            return Response.getOk(storeId);
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
            ProductRequest toAdd = ProductRequest.from(request.payload());
            proxy.addProduct(toAdd.storeId(), toAdd.product(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String updateProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toUpdate = ProductRequest.from(request.payload());
            proxy.updateProduct(toUpdate.storeId(), toUpdate.product(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removeProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toRemove = ProductRequest.from(request.payload());
            proxy.removeProduct(toRemove.storeId(), toRemove.product().getProductId(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String disableProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toDisable = ProductRequest.from(request.payload());
            proxy.disableProduct(toDisable.storeId(), toDisable.product().getProductId(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String enableProduct(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toEnable = ProductRequest.from(request.payload());
            proxy.enableProduct(toEnable.storeId(), toEnable.product().getProductId(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String setProductQuantity(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toSet = ProductRequest.from(request.payload());
            proxy.setProductQuantity(toSet.storeId(), toSet.product().getProductId(), toSet.quantity(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String getProductQuantity(String json) {
        Request request = Request.from(json);
        try {
            ProductRequest toGet = ProductRequest.from(request.payload());
            return Response.getOk(proxy.getProductQuantity(toGet.storeId(), toGet.product().getProductId(), request.userId(), request.token()));
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String getProduct(String json){
        Request request = Request.from(json);
        try {
            String productId = request.payload();
            return Response.getOk(proxy.getProduct(productId, request.userId(), request.token()));
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String getStoreProducts(String json) {
        Request request = Request.from(json);
        try {
            String storeId = request.payload();
            Map<Boolean, List<Product>> storeProducts = proxy.getStoreProducts(storeId, request.userId(), request.token());
            String ok = Response.getOk(storeProducts);
            return ok;
        } catch (NoPermissionException | AuthenticationFailedException | StoreException e) {
            return Response.getError(e);
        }
    }

    public String addOwner(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toAdd = StoreStaffRequest.from(request.payload());
            proxy.addOwner(toAdd.sourceActor(), toAdd.storeId(), toAdd.targetActor(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addManager(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toAdd = StoreStaffRequest.from(request.payload());
            proxy.addManager(toAdd.sourceActor(), toAdd.storeId(), toAdd.targetActor(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removeOwner(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toRemove = StoreStaffRequest.from(request.payload());
            proxy.removeOwner(toRemove.sourceActor(), toRemove.storeId(), toRemove.targetActor(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removeManager(String json) {
        Request request = Request.from(json);
        try {
            StoreStaffRequest toRemove = StoreStaffRequest.from(request.payload());
            proxy.removeManager(toRemove.sourceActor(), toRemove.storeId(), toRemove.targetActor(), request.userId(), request.token());
            return Response.getOk();
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addPermissionToManager(String json) {
        Request request = Request.from(json);
        try {
            StorePermissionRequest toAdd = StorePermissionRequest.from(request.payload());
            boolean result = proxy.addPermissionToManager(toAdd.storeId(), toAdd.targetActor(), StoreActions.valueOf(toAdd.action()), request.userId(), request.token());
            return Response.getOk(result);
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String removePermissionFromManager(String json) {
        Request request = Request.from(json);
        try {
            StorePermissionRequest toRemove = StorePermissionRequest.from(request.payload());
            boolean result = proxy.removePermissionFromManager(toRemove.storeId(), toRemove.targetActor(), StoreActions.valueOf(toRemove.action()), request.userId(), request.token());
            return Response.getOk(result);
        } catch (StoreException | NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String getStoreRolesInformation(String json){
        Request request = Request.from(json);
        try {
            List<StorePosition> result = proxy.getStoreRolesInformation(request.payload(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String getStoreTransactionHistory(String json){
        Request request = Request.from(json);
        try {
            List<Transaction> result = proxy.getStoreTransactionHistory(request.payload(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (NoPermissionException | AuthenticationFailedException e) {
            return Response.getError(e);
        }
    }

    public String addDiscountRuleByCFG(String json) {
        Request request = Request.from(json);
        try {
            DiscountCFGRequest discountCFGRequest = DiscountCFGRequest.from(request.payload());
            String result = proxy.addDiscountRuleByCFG(discountCFGRequest.StoreID(), discountCFGRequest.cfg(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (AuthenticationFailedException | DiscountPolicyException | StoreException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String getDiscountRuleCFG(String json) {
        Request request = Request.from(json);
        try {
            String result = proxy.getDiscountRuleCFG(request.payload(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (AuthenticationFailedException | StoreException e) {
            return Response.getError(e);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    public String addDiscountRuleByDTO(String json) {
        Request request = Request.from(json);
        try {
            DiscountDTORequest discountDTORequest = DiscountDTORequest.from(request.payload());
            String result = proxy.addDiscountRuleByDTO(discountDTORequest.StoreID(), discountDTORequest.discountComponentDTO(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (AuthenticationFailedException | DiscountPolicyException | StoreException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String getDiscountRuleDTO(String json) {
        Request request = Request.from(json);
        try {
            DiscountComponentDTO result = proxy.getDiscountRuleDTO(request.payload(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (AuthenticationFailedException | StoreException e) {
            return Response.getError(e);
        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    public String deleteAllDiscounts(String json) {
        Request request = Request.from(json);
        try {
            DiscountCFGRequest discountCFGRequest = DiscountCFGRequest.from(request.payload());
            boolean result = proxy.deleteAllDiscounts(discountCFGRequest.StoreID(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (AuthenticationFailedException | StoreException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String changePurchasePolicy(String json) {
        Request request = Request.from(json);
        try {
            PurchasePolicyRequest purchasePolicyRequest = PurchasePolicyRequest.from(request.payload());
            proxy.changePurchasePolicy(purchasePolicyRequest.StoreID(), purchasePolicyRequest.purchaseRule(), request.userId(), request.token());
            return Response.getOk();
        } catch (AuthenticationFailedException | StoreException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String getPurchasePolicyDTO(String json) {
        Request request = Request.from(json);
        try {
            PurchaseRuleDTO result = proxy.getPurchasePolicyDTO(request.payload(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (AuthenticationFailedException | StoreException | NoPermissionException e) {
            return Response.getError(e);
        }
    }

    public String removePurchasePolicy(String json) {
        Request request = Request.from(json);
        try {
            boolean result = proxy.removePurchasePolicy(request.payload(), request.userId(), request.token());
            return Response.getOk(result);
        } catch (AuthenticationFailedException | StoreException | NoPermissionException e) {
            return Response.getError(e);
        }
    }
}
