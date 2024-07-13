package com.amazonas.backend.business.permissions.proxies;

import com.amazonas.backend.business.authentication.AuthenticationController;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.backend.business.stores.discountPolicies.DiscountPolicyException;
import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.common.PurchaseRuleDTO.PurchaseRuleDTO;
import com.amazonas.common.dtos.StoreDetails;
import com.amazonas.common.permissions.actions.MarketActions;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.backend.business.stores.StoresController;
import com.amazonas.common.dtos.StorePosition;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.exceptions.AuthenticationFailedException;
import com.amazonas.backend.exceptions.NoPermissionException;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.requests.stores.GlobalSearchRequest;
import com.amazonas.common.requests.stores.ProductSearchRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component("storeProxy")
public class StoreProxy extends ControllerProxy {

    private final StoresController real;

    public StoreProxy(StoresController storesController, PermissionsController perm, AuthenticationController auth) {
        super(perm, auth);
        this.real = storesController;
    }

    public void addStore(String ownerID, String name, String description, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.CREATE_STORE);
        real.addStore(ownerID, name, description);
    }

    public boolean openStore(String storeId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.OPEN_STORE);
        return real.openStore(storeId);
    }

    public boolean closeStore(String storeId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.CLOSE_STORE);
        return real.closeStore(storeId);
    }

    public void addProduct(String storeId, Product toAdd, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ADD_PRODUCT);
        real.addProduct(storeId, toAdd);
    }

    public void updateProduct(String storeId, Product toUpdate, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.UPDATE_PRODUCT);
        real.updateProduct(storeId, toUpdate);
    }

    public void removeProduct(String storeId, String productId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.REMOVE_PRODUCT);
        real.removeProduct(storeId, productId);
    }

    public void disableProduct(String storeId, String productId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.DISABLE_PRODUCT);
        real.disableProduct(storeId, productId);
    }

    public void enableProduct(String storeId, String productId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ENABLE_PRODUCT);
        real.enableProduct(storeId, productId);
    }

    public void setProductQuantity(String storeId, String productId, Integer quantity, String userId, String token) throws NoPermissionException, AuthenticationFailedException, StoreException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.SET_PRODUCT_QUANTITY);
        real.setProductQuantity(storeId, productId, quantity);
    }

    public int getProductQuantity(String storeId, String productId, String userId, String token) throws NoPermissionException, AuthenticationFailedException, StoreException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.GET_PRODUCT_QUANTITY);
        return real.getProductQuantity(storeId, productId);
    }

    public Map<Boolean,List<Product>> getStoreProducts(String storeId, String userId, String token) throws StoreException, NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.VIEW_STORES);
        return real.getStoreProducts(storeId);
    }

    public void addOwner(String logged, String storeId, String username, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ADD_OWNER);
        real.addOwner(username, storeId, logged);
    }

    public void addManager(String logged, String storeId, String username, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ADD_MANAGER);
        real.addManager(logged, storeId, username);
    }

    public void removeOwner(String logged, String storeId, String username, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.REMOVE_OWNER);
        real.removeOwner(username, storeId, logged);
    }

    public void removeManager(String logged, String storeId, String username, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.REMOVE_MANAGER);
        real.removeManager(logged, storeId, username);
    }

    public boolean addPermissionToManager(String storeId, String managerId, StoreActions actions, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.ADD_PERMISSION_TO_MANAGER);
        return real.addPermissionToManager(storeId, managerId, actions);
    }

    public boolean removePermissionFromManager(String storeId, String managerId, StoreActions actions, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.REMOVE_PERMISSION_FROM_MANAGER);
        return real.removePermissionFromManager(storeId, managerId, actions);
    }

    public List<Product> searchProductsGlobally(GlobalSearchRequest request, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.SEARCH_PRODUCTS);
        return real.searchProductsGlobally(request);
    }

    public List<StoreDetails> searchStoresGlobally(String keyword, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.SEARCH_STORES);
        return real.searchStoresGlobally(keyword);
    }

    public List<Product> searchProductsInStore(String storeId, ProductSearchRequest request, String userId, String token) throws StoreException,AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,MarketActions.SEARCH_PRODUCTS);
        return real.searchProductsInStore(storeId, request);
    }

    public List<StorePosition> getStoreRolesInformation(String storeId, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.VIEW_ROLES_INFORMATION);
        return real.getStoreRolesInformation(storeId);
    }

    public List<Transaction> getStoreTransactionHistory(String storeId, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.VIEW_STORE_TRANSACTIONS);
        return real.getStoreTransactionHistory(storeId);
    }

    public StoreDetails getStoreDetails(String storeId, String userId, String token) throws NoPermissionException, AuthenticationFailedException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.VIEW_STORES);
        return real.getStoreDetails(storeId);
    }

    public Product getProduct(String productId, String userId, String token) throws NoPermissionException, AuthenticationFailedException, StoreException {
        authenticateToken(userId, token);
        checkPermission(userId, MarketActions.VIEW_PRODUCTS);
        return real.getProduct(productId);
    }

    public String addDiscountRuleByCFG(String storeId,String cfg, String userId, String token) throws StoreException, DiscountPolicyException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.EDIT_DISCOUNT);
        return real.addDiscountRuleByCFG(storeId, cfg);

    }

    public String getDiscountRuleCFG(String storeId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,MarketActions.VIEW_STORES);
        return real.getDiscountRuleCFG(storeId);
    }

    public String addDiscountRuleByDTO(String storeId,DiscountComponentDTO dto, String userId, String token) throws StoreException, DiscountPolicyException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.EDIT_DISCOUNT);
        return real.addDiscountRuleByDTO(storeId, dto);
    }

    public DiscountComponentDTO getDiscountRuleDTO(String storeId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,MarketActions.VIEW_STORES);
        return real.getDiscountRuleDTO(storeId);
    }

    public boolean deleteAllDiscounts(String storeId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.EDIT_DISCOUNT);
        return real.deleteAllDiscounts(storeId);
    }

    public PurchaseRuleDTO getPurchasePolicyDTO(String storeId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,MarketActions.VIEW_STORES);
        return real.getPurchasePolicyDTO(storeId);
    }

    public boolean deleteAllPurchasePolicies(String storeId, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.EDIT_POLICY);
        return real.deleteAllPurchasePolicies(storeId);
    }

    public void changePurchasePolicy(String storeId, PurchaseRuleDTO purchaseRuleDTO, String userId, String token) throws StoreException, AuthenticationFailedException, NoPermissionException {
        authenticateToken(userId, token);
        checkPermission(userId,storeId, StoreActions.EDIT_POLICY);
        real.changePurchasePolicy(storeId, purchaseRuleDTO);
    }

}
