package com.amazonas.backend.business.stores;

import com.amazonas.backend.business.inventory.ProductInventory;
import com.amazonas.backend.business.permissions.PermissionsController;
import com.amazonas.common.DiscountDTOs.DiscountComponentDTO;
import com.amazonas.backend.business.stores.discountPolicies.DiscountManager;
import com.amazonas.backend.business.stores.discountPolicies.ProductAfterDiscount;
import com.amazonas.backend.business.stores.discountPolicies.ProductWithQuantitiy;
import com.amazonas.backend.business.stores.purchasePolicy.PurchasePolicyManager;
import com.amazonas.common.PurchaseRuleDTO.PurchaseRuleDTO;
import com.amazonas.backend.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.backend.business.stores.reservations.Reservation;
import com.amazonas.backend.business.stores.reservations.ReservationFactory;
import com.amazonas.backend.business.stores.storePositions.AppointmentSystem;
import com.amazonas.common.dtos.StorePosition;
import com.amazonas.common.dtos.StoreRole;
import com.amazonas.backend.business.userProfiles.RegisteredUser;
import com.amazonas.common.dtos.Transaction;
import com.amazonas.backend.exceptions.StoreException;
import com.amazonas.backend.repository.TransactionRepository;
import com.amazonas.common.dtos.Product;
import com.amazonas.common.dtos.StoreDetails;
import com.amazonas.common.permissions.actions.StoreActions;
import com.amazonas.common.requests.stores.ProductSearchRequest;
import com.amazonas.common.utils.Rating;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.*;

public class Store {

    // Static final variables
    private static final int FIVE_MINUTES = 5 * 60;
    private static final long reservationTimeoutSeconds = FIVE_MINUTES;

    // Final instance variables
    private final ReservationFactory reservationFactory;
    private final PendingReservationMonitor pendingReservationMonitor;
    private final PermissionsController permissionsController;
    private final TransactionRepository repository;
    private final ProductInventory inventory;
    private final AppointmentSystem appointmentSystem;
    private final DiscountManager discountManager;
    private final PurchasePolicyManager purchasePolicyManager;
    private final ReadWriteLock lock;
    private final String storeId;
    private final String storeName;

    // Non-final instance variables
    private boolean isOpen;
    private Rating storeRating;
    private String storeDescription;

    public Store(String storeId,
                 String storeName,
                 String description,
                 Rating rating,
                 ProductInventory inventory,
                 AppointmentSystem appointmentSystem,
                 ReservationFactory reservationFactory,
                 PendingReservationMonitor pendingReservationMonitor,
                 PermissionsController permissionsController,
                 TransactionRepository transactionRepository) {
        this.appointmentSystem = appointmentSystem;
        this.reservationFactory = reservationFactory;
        this.pendingReservationMonitor = pendingReservationMonitor;
        this.inventory = inventory;
        this.storeId = storeId;
        this.storeName = storeName;
        this.storeDescription = description;
        this.storeRating = rating;
        this.permissionsController = permissionsController;
        this.repository = transactionRepository;
        this.discountManager = new DiscountManager();
        this.purchasePolicyManager = new PurchasePolicyManager();
        lock = new ReadWriteLock();
        isOpen = true;
    }

    public StoreDetails getDetails() {
        return new StoreDetails(storeId, storeName, storeDescription, storeRating);
    }

    //====================================================================== |
    //============================= MANAGEMENT ============================= |
    //====================================================================== |
    public boolean openStore(){

        try{
            lock.acquireWrite();
            if(isOpen) {
                return false;
            } else{
                isOpen = true;
                return true;
            }
        } finally {
            lock.releaseWrite();
        }
    }

    public boolean closeStore(){
        try{

            lock.acquireWrite();

            if(isOpen){
                isOpen = false;
                return true;
            }
            else {
                return false;
            }

        }finally {
            lock.releaseWrite();
        }
    }

    public boolean isOpen(){
        return isOpen;
    }

    public void checkIfOpen() throws StoreException {
        if(!isOpen) {
            throw new StoreException("Store is closed");
        }
    }

    //====================================================================== |
    //========================== PAID ORDERS =============================== |
    //====================================================================== |

    // TODO: All this can probably be moved to the controller

    public Collection<Transaction> getPendingShipmentOrders() throws StoreException {
        try{
            lock.acquireRead();
            checkIfOpen();

            return repository.getWaitingShipment(storeId);
        } finally {
            lock.releaseRead();
        }
    }

    public void setOrderShipped(String transactionId) throws StoreException {
        try{
            lock.acquireWrite();
            checkIfOpen();

            Transaction transaction = repository.getTransactionById(transactionId);
            transaction.setShipped();
            repository.updateTransaction(transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    public void setOrderDelivered(String transactionId) throws StoreException {
        try{
            lock.acquireWrite();
            checkIfOpen();

            Transaction transaction = repository.getTransactionById(transactionId);
            transaction.setDelivered();
            repository.updateTransaction(transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    public void setOrderCancelled(String transactionId) throws StoreException {
        try{
            lock.acquireWrite();
            checkIfOpen();

            Transaction transaction = repository.getTransactionById(transactionId);
            transaction.setCancelled();
            repository.updateTransaction(transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    //====================================================================== |
    //============================= PRODUCTS =============================== |
    //====================================================================== |

    public double calculatePrice(Map<String,Integer> products) {
        try {
            lock.acquireRead();
            List<ProductWithQuantitiy> productsWithQuantitiy = new ArrayList<>();
            for (String productID : products.keySet()){
                productsWithQuantitiy.add(new ProductWithQuantitiy(inventory.getProduct(productID), products.get(productID)));
            }
            ProductAfterDiscount[] res = discountManager.applyDiscountPolicy(productsWithQuantitiy);
            double ret = 0.0;
            for (ProductAfterDiscount productAfterDiscount : res) {
                ret += productAfterDiscount.priceAfterDiscount() * productAfterDiscount.quantity();
            }
            return ret;
        } catch (StoreException e) {
            return -1;
        } finally {
            lock.releaseRead();
        }
    }

    public List<Product> searchProduct(ProductSearchRequest request) {
        try{
            lock.acquireRead();
            if(!isOpen){
                return List.of();
            }

            List<Product> toReturn = new LinkedList<>();
            for (Product product : inventory.getAllAvailableProducts()) {
                if(product.getPrice() < request.minPrice() || product.getPrice() > request.maxPrice()){
                    continue;
                }
                if(product.getRating().ordinal() < request.productRating().ordinal()){
                    continue;
                }
                if(!request.productName().isBlank() && product.getProductName().toLowerCase().contains(request.productName())){
                    toReturn.add(product);
                    continue;
                }
                if(!request.productCategory().isBlank() && product.getCategory().toLowerCase().contains(request.productCategory())){
                    toReturn.add(product);
                    continue;
                }
                if(!request.productName().isBlank() && product.getDescription().toLowerCase().contains(request.productName())){
                    toReturn.add(product);
                    continue;
                }

                Set<String> keywords = product.getKeyWords();
                // If no keywords are specified, nothing is added TODO: Check if this is the desired behavior

                for (String keyword : request.keyWords()) {
                    if(keywords.stream().anyMatch(s -> s.contains(keyword))){
                        if (!toReturn.contains(product)) {
                            toReturn.add(product);
                        }
                    }
                }
            }
            return toReturn;
        } finally{
            lock.releaseRead();
        }
    }

    public int availableCount(String productId) throws StoreException {

        try{
            lock.acquireRead();
            checkIfOpen();
            return inventory.getQuantity(productId);
        } finally {
            lock.releaseRead();
        }
    }

    public String addProduct(Product toAdd) throws StoreException {
        try{
            lock.acquireWrite();
            checkIfOpen();
            toAdd.setStoreId(storeId);
            return inventory.addProduct(toAdd);
        } finally {
            lock.releaseWrite();
        }
    }

    public void removeProduct(String productIdToRemove) throws StoreException {
        try {
            lock.acquireWrite();
            checkIfOpen();
            inventory.removeProduct(productIdToRemove);
        } finally {
            lock.releaseWrite();
        }

    }

    public void updateProduct(Product product) throws StoreException {
        try {
            lock.acquireWrite();
            checkIfOpen();
            inventory.updateProduct(product);
        } finally {
            lock.releaseWrite();
        }
     }

    public void enableProduct(String productId) throws StoreException {
        try {
            lock.acquireWrite();
            checkIfOpen();
            inventory.enableProduct(productId);
        } finally {
            lock.releaseWrite();
        }
    }

    public void disableProduct(String productId) throws StoreException {
        try {
            lock.acquireWrite();
            checkIfOpen();
            inventory.disableProduct(productId);
        } finally {
            lock.releaseWrite();
        }
    }

    public int getProductQuantity(String productId) throws StoreException {
        try {
            lock.acquireRead();
            checkIfOpen();
            return inventory.getQuantity(productId);
        } finally {
            lock.releaseRead();
        }
    }

    public void setProductQuantity(String productId, int quantity) throws StoreException {
        try {
            lock.acquireWrite();
            checkIfOpen();
            inventory.setQuantity(productId, quantity);
        } finally {
            lock.releaseWrite();
        }
    }

    public Map<Boolean,List<Product>> getStoreProducts() throws StoreException {
        try {
            lock.acquireRead();
            checkIfOpen();
            return inventory.getProducts();
        } finally {
            lock.releaseRead();
        }
    }

    //====================================================================== |
    //=========================== RESERVATIONS ============================= |
    //====================================================================== |

    @Nullable
    public Reservation reserveProducts(Map<String,Integer> toReserve, String userId){
        try{
            lock.acquireWrite();
            if(!isOpen){
                return null;
            }

            // Check if the products are available
            for (var entry : toReserve.entrySet()) {
                String productId = entry.getKey();
                int quantity = entry.getValue();
                if (inventory.isProductDisabled(productId)) {
                    return null;
                } else if (inventory.getQuantity(productId) < quantity) {
                    return null;
                }
            }

            // Reserve the products
            for (var entry : toReserve.entrySet()) {
                String productId = entry.getKey();
                int quantity = entry.getValue();
                inventory.setQuantity(productId, inventory.getQuantity(productId) - quantity);
            }

            // Create the reservation
            Reservation reservation = reservationFactory.get(
                    userId,
                    storeId,
                    toReserve,
                    LocalDateTime.now().plusSeconds(reservationTimeoutSeconds));

            pendingReservationMonitor.addReservation(reservation);

            return reservation;
        } finally {
            lock.releaseWrite();
        }
    }

    public boolean cancelReservation(Reservation reservation) {
        try{
            lock.acquireWrite();
            if(!isOpen){
                return false;
            }

            if(reservation.isCancelled()){
                return false;
            }

            if(!reservation.storeId().equals(storeId)){
                return false;
            }

            reservation.setCancelled();

            // Return the reserved products to the inventory
            for(var entry : reservation.productIdToQuantity().entrySet()){
                String productId = entry.getKey();
                int quantity = entry.getValue();
                inventory.setQuantity(productId, inventory.getQuantity(productId) + quantity);
            }
            return true;
        } finally {
            lock.releaseWrite();
        }
    }

    //====================================================================== |
    //========================= STORE POSITIONS ============================ |
    //====================================================================== |

    // Synchronization is done in the AppointmentSystem class

    public void removeOwner(String logged, String username) {
        appointmentSystem.removeOwner(logged,username);
    }

    public void removeManager(String logged, String username) {
        appointmentSystem.removeManager(logged,username);
    }

    public void addManager(String logged, String username) {
        appointmentSystem.addManager(logged,username);
    }

    public void addOwner(String logged, String username) {
        appointmentSystem.addOwner(logged,username);
        permissionsController.addPermission(username,storeId,StoreActions.ALL);
    }

    public List<StorePosition> getRolesInformation() {
        return appointmentSystem.getAllRoles();
    }

    public List<String> getOwners(){
        return getRolesInformation().stream()
                .filter(storePosition -> storePosition.role() == StoreRole.STORE_OWNER)
                .map(StorePosition::userId)
                .toList();
    }

    //====================================================================== |
    //========================= STORE DISCOUNTS ============================ |
    //====================================================================== |

    public DiscountComponentDTO getDiscountPolicyDTO() throws StoreException {
        try {
            lock.acquireRead();
            return discountManager.getDiscountPolicyDTO();
        } finally {
            lock.releaseRead();
        }
    }

    public String getDiscountPolicyCFG() throws StoreException {
        try {
            lock.acquireRead();
            String ret = discountManager.getDiscountPolicyCFG();
            if (ret == null) {
                throw new StoreException("No discount policy found");
            }
            return ret;
        } finally {
            lock.releaseRead();
        }
    }

    public boolean deleteAllDiscounts() {
        try {
            lock.acquireWrite();
            return discountManager.deleteAllDiscounts();
        } finally {
            lock.releaseWrite();
        }
    }

    public ProductAfterDiscount[] applyDiscountPolicy(List<ProductWithQuantitiy> products) throws StoreException {
        try {
            lock.acquireRead();
            return discountManager.applyDiscountPolicy(products);
        } finally {
            lock.releaseRead();
        }
    }

    public String changeDiscountPolicy(DiscountComponentDTO discountComponentDTO) throws StoreException {
        try {
            lock.acquireWrite();
            return discountManager.changeDiscountPolicy(discountComponentDTO);
        } finally {
            lock.releaseWrite();
        }
    }


    //====================================================================== |
    //========================== STORE POLICIES ============================ |
    //====================================================================== |

    public PurchaseRuleDTO getPurchasePolicyDTO() throws StoreException {
        try {
            lock.acquireRead();
            return purchasePolicyManager.getPurchasePolicy();
        } finally {
            lock.releaseRead();
        }
    }

    public boolean deleteAllPurchasePolicies() {
        try {
            lock.acquireWrite();
            return purchasePolicyManager.deletePurchasePolicy();
        } finally {
            lock.releaseWrite();
        }
    }

    public boolean isPurchasePolicySatisfied(List<ProductWithQuantitiy> products, RegisteredUser user) throws StoreException {
        try {
            lock.acquireRead();
            return purchasePolicyManager.isSatisfied(products, user);
        } finally {
            lock.releaseRead();
        }
    }

    public void changePurchasePolicy(PurchaseRuleDTO purchaseRuleDTO) throws StoreException {
        try {
            lock.acquireWrite();
            purchasePolicyManager.changePurchasePolicy(purchaseRuleDTO);
        } finally {
            lock.releaseWrite();
        }
    }

    //====================================================================== |
    //======================= STORE PERMISSIONS ============================ |
    //====================================================================== |

    // Synchronization is done in the PermissionsController class

    public boolean addPermissionToManager(String managerId, StoreActions action) throws StoreException {

        StoreRole role = appointmentSystem.getRoleOfUser(managerId);

        if(role != StoreRole.STORE_MANAGER){
            throw new StoreException("User is not a manager");
        }

        switch(action){
            case ADD_PRODUCT,REMOVE_PRODUCT,UPDATE_PRODUCT,ENABLE_PRODUCT,DISABLE_PRODUCT-> {
                return permissionsController.addPermission(managerId,storeId,action);
            }
            default -> {
                return false;
            }
        }
    }

    public boolean removePermissionFromManager(String managerId, StoreActions action) throws StoreException {

        StoreRole role = appointmentSystem.getRoleOfUser(managerId);

        if(role != StoreRole.STORE_MANAGER){
            throw new StoreException("User is not a manager");
        }

        switch(action){
            case ADD_PRODUCT,REMOVE_PRODUCT,UPDATE_PRODUCT,ENABLE_PRODUCT,DISABLE_PRODUCT-> {
                return permissionsController.removePermission(managerId,storeId,action);
            }
            default -> {
                return false;
            }
        }
    }
    //====================================================================== |
    //========================= GETTERS SETTERS ============================ |
    //====================================================================== |

    public Rating getStoreRating() {
        return storeRating;
    }

    public String getStoreId() {
        return storeId;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreRating(Rating storeRating) {
        this.storeRating = storeRating;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    public String getStoreName() {
        return storeName;
    }
}
