package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.business.permissions.PermissionsController;
import com.amazonas.business.permissions.actions.StoreActions;
import com.amazonas.business.stores.search.SearchRequest;
import com.amazonas.business.stores.reservations.Reservation;
import com.amazonas.business.stores.reservations.ReservationFactory;
import com.amazonas.business.stores.reservations.PendingReservationMonitor;
import com.amazonas.business.stores.storePositions.AppointmentSystem;
import com.amazonas.business.stores.storePositions.StorePosition;
import com.amazonas.business.stores.storePositions.StoreRole;
import com.amazonas.business.transactions.Transaction;
import com.amazonas.exceptions.StoreException;
import com.amazonas.repository.TransactionRepository;
import com.amazonas.utils.Rating;
import com.amazonas.utils.ReadWriteLock;
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

    public Collection<Transaction> getPendingShipmentOrders(){
        try{
            lock.acquireRead();
            return repository.getWaitingShipment(storeId);
        } finally {
            lock.releaseRead();
        }
    }

    public void setOrderShipped(String transactionId){
        try{
            lock.acquireWrite();
            Transaction transaction = repository.getTransactionById(transactionId);
            transaction.setShipped();
            repository.updateTransaction(transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    public void setOrderDelivered(String transactionId){
        try{
            lock.acquireWrite();
            Transaction transaction = repository.getTransactionById(transactionId);
            transaction.setDelivered();
            repository.updateTransaction(transaction);
        } finally {
            lock.releaseWrite();
        }
    }

    public void setOrderCancelled(String transactionId){
        try{
            lock.acquireWrite();
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

    public double calculatePrice(Map<String,Integer> products){
        // TODO: implement this in later versions
        return 0.0;
    }

    public List<Product> searchProduct(SearchRequest request) {
        try{
            lock.acquireRead();
            List<Product> toReturn = new LinkedList<>();
            for (Product product : inventory.getAllAvailableProducts()) {
                if(product.price() < request.getMinPrice() || product.price() > request.getMaxPrice()){
                    continue;
                }
                if(product.rating().ordinal() < request.getProductRating().ordinal()){
                    continue;
                }
                if(!request.getProductName().isBlank() && product.productName().toLowerCase().contains(request.getProductName())){
                    toReturn.add(product);
                    continue;
                }
                if(!request.getProductCategory().isBlank() && product.category().toLowerCase().contains(request.getProductCategory())){
                    toReturn.add(product);
                    continue;
                }
                if(!request.getProductName().isBlank() && product.description().toLowerCase().contains(request.getProductName())){
                    toReturn.add(product);
                    continue;
                }
                Set<String> keywords = product.keyWords();
                if(request.getKeyWords().stream().anyMatch(keywords::contains)){
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

    public String addProduct(Product toAdd) throws StoreException {
        try{
            lock.acquireWrite();
            checkIfOpen();
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

    public void enableProduct(String productId) {
        try {
            lock.acquireWrite();
            inventory.enableProduct(productId);
        } finally {
            lock.releaseWrite();
        }
    }

    public void disableProduct(String productId){
        try {
            lock.acquireWrite();
            inventory.disableProduct(productId);
        } finally {
            lock.releaseWrite();
        }
    }

    //====================================================================== |
    //=========================== RESERVATIONS ============================= |
    //====================================================================== |

    @Nullable
    public Reservation reserveProducts(Map<String,Integer> toReserve, String userId){
        try{
            lock.acquireWrite();

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
    }

    public List<StorePosition> getRolesInformation() {
        return appointmentSystem.getAllRoles();
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
