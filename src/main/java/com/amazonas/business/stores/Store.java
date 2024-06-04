package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.stores.policies.SalesPolicy;
import com.amazonas.business.stores.search.SearchRequest;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.stores.reservations.ReservationFactory;
import com.amazonas.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.business.stores.storePositions.AppointmentSystem;
import com.amazonas.business.stores.storePositions.StoreRole;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.exceptions.StoreException;
import com.amazonas.repository.TransactionRepository;
import com.amazonas.utils.Pair;
import com.amazonas.utils.Rating;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.*;

public class Store {

    private static final int FIVE_MINUTES = 5 * 60;
    private final ReservationFactory reservationFactory;
    private final PendingReservationMonitor pendingReservationMonitor;
    private final PermissionsController permissionsController;
    private final TransactionRepository repository;
    private final ProductInventory inventory;
    private final AppointmentSystem appointmentSystem;
    private final List<SalesPolicy> salesPolicies;
    private final ReadWriteLock lock;

    private final String storeName;
    private final String storeId;

    private String storeDescription;
    private Rating storeRating;
    private boolean isOpen;
    private long reservationTimeoutSeconds;

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
        this.reservationTimeoutSeconds = FIVE_MINUTES;
        this.salesPolicies = new LinkedList<>();
        lock = new ReadWriteLock();
        isOpen = true;
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

    public void addSalePolicy(SalesPolicy salesPolicy){
        try{
            lock.acquireWrite();
            salesPolicies.add(salesPolicy);
        } finally {
            lock.releaseWrite();
        }
    }

    public void removeSalePolicy(SalesPolicy salesPolicy){
        try{
            lock.acquireWrite();
            salesPolicies.remove(salesPolicy);
        } finally {
            lock.releaseWrite();
        }
    }

    public boolean isOpen(){
        return isOpen;
    }

    //====================================================================== |
    //========================== PAID ORDERS =============================== |
    //====================================================================== |

    public Collection<Transaction> getPendingShipmentOrders(){
        try{
            lock.acquireRead();
            return repository.getWaitingShipment(storeId);
        } finally {
            lock.releaseRead();
        }
    }

    public void setShipped(String transactionId){
        try{
            lock.acquireWrite();
            Transaction transaction = repository.getTransaction(transactionId);
            transaction.setShipped();
            repository.saveTransaction(transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    public void setDelivered(String transactionId){
        try{
            lock.acquireWrite();
            Transaction transaction = repository.getTransaction(transactionId);
            transaction.setDelivered();
            repository.saveTransaction(transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    public void setCancelled(String transactionId){
        try{
            lock.acquireWrite();
            Transaction transaction = repository.getTransaction(transactionId);
            transaction.setCancelled();
            repository.saveTransaction(transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    //====================================================================== |
    //============================= PRODUCTS =============================== |
    //====================================================================== |

    public double calculatePrice(Map<Product,Integer> products){
        try{
            lock.acquireRead();

            double sum = 0;
            for(var entry : products.entrySet()){
                sum += entry.getKey().price() * entry.getValue();
            }
            return sum;
        } finally {
            lock.releaseRead();
        }
    }

    private double applyDiscount(Pair<Product,Integer> pair){

        try{
            lock.acquireRead();

            Product product = pair.first();
            Integer quantity = pair.second();
            int maxQuantity = 0;
            int maxDiscount = 0;
            for(SalesPolicy salesPolicy: salesPolicies){
                if(salesPolicy.getProductID().equals(product.productId())){
                    if(maxQuantity <= salesPolicy.getProductQuantity()) {
                        maxQuantity = salesPolicy.getProductQuantity();
                        maxDiscount = salesPolicy.getDiscount();
                    }
                }
            }
            if(maxQuantity == 0){
                return product.price() * quantity;
            }
            return product.price() * (100 - maxDiscount)*0.01;
        } finally {
            lock.releaseRead();
        }
    }

    public List<Product> searchProduct(SearchRequest request) {

        try{
            lock.acquireRead();
            List<Product> toReturn = new LinkedList<>();
            for (Product product : inventory.getAllAvailableProducts()) {

                // Check if the product matches the search request
                if((product.price() >= request.getMinPrice() && product.price() <= request.getMaxPrice())
                        || product.rating().ordinal() >= request.getProductRating().ordinal()
                        || product.productName().toLowerCase().contains(request.getProductName())
                        || product.category().toLowerCase().contains(request.getProductCategory())
                        || product.description().toLowerCase().contains(request.getProductName())
                        || request.getKeyWords().stream().anyMatch(product.keyWords()::contains))
                {
                    toReturn.add(product);
                }
            }
            return toReturn;
        } finally{
            lock.releaseRead();
        }
    }

    public int availableCount(String productId){

        try{
            lock.acquireRead();
            return inventory.getQuantity(productId);
        } finally {
            lock.releaseRead();
        }
    }

    public void addProduct(Product toAdd) throws StoreException {
        try{
            lock.acquireWrite();

            if(isOpen) {
                if(inventory.nameExists(toAdd.productName())) {
                    inventory.addProduct(toAdd);
                }
                else {
                    throw new StoreException("product name exists");
                }
            }
            else {
                throw new StoreException("store is closed");
            }

        } finally {
            lock.releaseWrite();
        }
    }

    public void removeProduct(String productIdToRemove) throws StoreException {
        try {
            lock.acquireWrite();

            if (isOpen) {
                boolean check = inventory.removeProduct(productIdToRemove);
                if(!check){
                    //product wasn't removed
                    throw new StoreException("product wasn't removed - no product in system");
                }
            }
            else {
                throw new StoreException("product wasn't removed - store closed");
            }

        } finally {
            lock.releaseWrite();
        }

    }

    public void updateProduct(Product product){
        lock.acquireWrite();
        try {
            inventory.updateProduct(product);
        } finally {
            lock.releaseWrite();
        }
     }

    public void enableProduct(String productId){
        lock.acquireWrite();
        try {
            inventory.enableProduct(productId);
        } finally {
            lock.releaseWrite();
        }
    }

    public void disableProduct(String productId){
        lock.acquireWrite();
        try {
            inventory.disableProduct(productId);
        } finally {
            lock.releaseWrite();
        }
    }

    //====================================================================== |
    //=========================== RESERVATIONS ============================= |
    //====================================================================== |

    @Nullable
    public Reservation reserveProducts(Map<Product,Integer> toReserve){
        try{
            lock.acquireWrite();

            // Check if the products are available
            for (var entry : toReserve.entrySet()) {
                String productId = entry.getKey().productId();
                int quantity = entry.getValue();
                if (inventory.isProductDisabled(productId) && inventory.getQuantity(productId) < quantity) {
                    return null;
                }
            }

            // Reserve the products
            for (var entry : toReserve.entrySet()) {
                String productId = entry.getKey().productId();
                int quantity = entry.getValue();
                inventory.setQuantity(productId, inventory.getQuantity(productId) - quantity);
            }

            // Create the reservation
            Reservation reservation = reservationFactory.get(
                    storeId,
                    toReserve,
                    LocalDateTime.now().plusSeconds(reservationTimeoutSeconds));

            pendingReservationMonitor.addReservation(reservation);

            return reservation;
        } finally {
            lock.releaseWrite();
        }
    }

    public void cancelReservation(Reservation reservation){
        try{
            lock.acquireWrite();

            reservation.setCancelled();

            // Return the reserved products to the inventory
            for(var entry : reservation.productToQuantity().entrySet()){
                String productId = entry.getKey().productId();
                int quantity = entry.getValue();
                inventory.setQuantity(productId, inventory.getQuantity(productId) + quantity);
            }

        } finally {
            lock.releaseWrite();
        }
    }

    public void setReservationTimeoutSeconds(long reservationTimeoutSeconds) {
        try{
            lock.acquireWrite();
            this.reservationTimeoutSeconds = reservationTimeoutSeconds;
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
