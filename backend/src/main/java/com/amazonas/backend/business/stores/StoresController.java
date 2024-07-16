package com.amazonas.backend.business.stores;

import com.amazonas.backend.business.stores.discountPolicies.DiscountPolicyException;
import com.amazonas.backend.business.stores.discountPolicies.Translator;
import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.common.PurchaseRuleDTO.PurchaseRuleDTO;
import com.amazonas.backend.repository.ProductRepository;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.StoreDetails;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.backend.business.stores.factories.StoreFactory;
import com.amazonas.backend.business.stores.storePositions.StorePosition;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.backend.repository.StoreRepository;
import com.amazonas.backend.repository.TransactionRepository;
import com.amazonas.common.requests.stores.GlobalSearchRequest;
import com.amazonas.common.requests.stores.ProductSearchRequest;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Component("storesController")
public class StoresController {
    private final StoreFactory storeFactory;
    private final StoreRepository repository;
    private final TransactionRepository transactionRepository;
    private final ProductRepository productRepository;

    public StoresController(StoreFactory storeFactory, StoreRepository storeRepository, TransactionRepository transactionRepository, ProductRepository productRepository){
        this.storeFactory = storeFactory;
        this.repository = storeRepository;
        this.transactionRepository = transactionRepository;
        this.productRepository = productRepository;
    }

    public String addStore(String ownerID,String name, String description) throws StoreException {
        if(doesNameExists(name))
            throw new StoreException("Store name already exists");
        Store toAdd = storeFactory.get(ownerID,name,description);
        repository.saveStore(toAdd);
        return toAdd.getStoreId();
    }

    public boolean openStore(String storeId){
        return getStore(storeId).openStore();
    }

    public boolean closeStore(String storeId){
        return getStore(storeId).closeStore();
    }

    private boolean doesNameExists(String name){
        //TODO: replace this with a database query
        return repository.storeNameExists(name);
    }

    public void addProduct(String storeId,Product toAdd) throws StoreException {
        getStore(storeId).addProduct(toAdd);
    }

    public void updateProduct(String storeId,Product toUpdate) throws StoreException {
        getStore(storeId).updateProduct(toUpdate);
    }

    public void removeProduct(String storeId,String productId) throws StoreException {
        getStore(storeId).removeProduct(productId);
    }

    public void disableProduct(String storeId,String productId) throws StoreException {
        getStore(storeId).disableProduct(productId);
    }

    public void enableProduct(String storeId,String productId) throws StoreException {
        getStore(storeId).enableProduct(productId);
    }

    public void setProductQuantity(String storeId, String productId, Integer quantity) throws StoreException {
        getStore(storeId).setProductQuantity(productId, quantity);
    }

    public int getProductQuantity(String storeId, String productId) throws StoreException {
        return getStore(storeId).getProductQuantity(productId);
    }

    public Map<Boolean,List<Product>> getStoreProducts(String storeId) throws StoreException {
        return getStore(storeId).getStoreProducts();
    }

    public void addOwner(String username, String storeId, String logged){
        getStore(storeId).addOwner(logged,username);
    }

    public void addManager(String logged, String storeId, String username){
        getStore(storeId).addManager(logged,username);
    }

    public void removeOwner(String username,String storeId, String logged){
        getStore(storeId).removeOwner(logged,username);
    }

    public void removeManager(String logged, String storeId,String username){
        getStore(storeId).removeManager(logged,username);
    }

    public boolean addPermissionToManager(String storeId,String managerId, StoreActions actions) throws StoreException {
        return getStore(storeId).addPermissionToManager(managerId,actions);
    }

    public boolean removePermissionFromManager(String storeId,String managerId, StoreActions actions) throws StoreException {
        return getStore(storeId).removePermissionFromManager(managerId,actions);
    }

    public Store getStore(String storeId){
        return repository.getStore(storeId);
    }

    public List<StoreDetails> searchStoresGlobally(String keyword) {
        List<StoreDetails> ret = new LinkedList<>();
        List<String> split = List.of(keyword.split(" "));
        for (Store store : repository.getAllStores()){
            for (String key : split){
                if (store.getDetails().getStoreName().contains(key)){
                    ret.add(store.getDetails());
                    break;
                }
            }
        }
        return ret;
    }

    public List<Product> searchProductsGlobally(GlobalSearchRequest request) {
        List<Product> ret = new LinkedList<>();
        for (Store store : repository.getAllStores()) {
            if (store.getStoreRating().ordinal() >= request.storeRating().ordinal()) {
                ret.addAll(store.searchProduct(request.productSearchRequest()));
            }
        }
        return ret;
    }

    public List<Product> searchProductsInStore(String storeId, ProductSearchRequest request) {
        return getStore(storeId).searchProduct(request);
    }

    public List<StorePosition> getStoreRolesInformation(String storeId) {
        return getStore(storeId).getRolesInformation();
    }

    public List<Transaction> getStoreTransactionHistory(String storeId) {
        return transactionRepository.getTransactionHistoryByStore(storeId);
    }

    public StoreDetails getStoreDetails(String storeId) {
        return getStore(storeId).getDetails();
    }

    public Product getProduct(String productId) throws StoreException {
        if(productRepository.getProduct(productId) == null){
            throw new StoreException("Product not found");
        }
        return productRepository.getProduct(productId);
    }

    public String addDiscountRuleByCFG(String storeId,String cfg) throws StoreException, DiscountPolicyException {
        return getStore(storeId).changeDiscountPolicy(Translator.translator(cfg));
    }

    public String getDiscountRuleCFG(String storeId) throws StoreException {
        return getStore(storeId).getDiscountPolicyCFG();
    }

    public String addDiscountRuleByDTO(String storeId, DiscountComponentDTO dto) throws StoreException, DiscountPolicyException {
        return getStore(storeId).changeDiscountPolicy(dto);
    }

    public DiscountComponentDTO getDiscountRuleDTO(String storeId) throws StoreException {
        return getStore(storeId).getDiscountPolicyDTO();
    }

    public boolean deleteAllDiscounts(String storeId) throws StoreException {
        return getStore(storeId).deleteAllDiscounts();
    }

    public PurchaseRuleDTO getPurchasePolicyDTO(String storeId) throws StoreException {
        return getStore(storeId).getPurchasePolicyDTO();
    }

    public boolean removePurchasePolicy(String storeId) throws StoreException {
        return getStore(storeId).deleteAllPurchasePolicies();
    }

    public void changePurchasePolicy(String storeId, PurchaseRuleDTO purchaseRuleDTO) throws StoreException {
        getStore(storeId).changePurchasePolicy(purchaseRuleDTO);
    }

}
