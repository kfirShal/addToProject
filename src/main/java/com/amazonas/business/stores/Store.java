package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.exceptions.StoreException;
import com.amazonas.utils.ErrorCollection;
import com.amazonas.utils.Pair;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

public class Store {

    private static final int FIVE_MINUTES = 5 * 60;

    private long reservationTimeoutSeconds;
    private final ProductInventory inventory;
    private final ConcurrentMap<String, Reservation> reservedProducts;
    private final Semaphore lock;
    private final Object waitObject = new Object();

    private String storeId;
    private String storeDescription;
    private Rating storeRating;
    private boolean isOpen;
    private ManagementNode managementTree;
    private Map<String, ManagementNode> managersList;
    private ManagementNode ownershipTree;
    private Map<String, ManagementNode> ownershipList;

    public Store(String storeId, String description, Rating rating, ProductInventory inventory, String ownerUseId) {
        this.reservationTimeoutSeconds = FIVE_MINUTES;
        this.inventory = inventory;
        this.storeId = storeId;
        this.storeDescription = description;
        this.storeRating = rating;
        this.managementTree = new ManagementNode(ownerUseId, null);
        this.managersList = new HashMap<>();
        this.ownershipTree = new ManagementNode(ownerUseId, null);
        this.ownershipList = new HashMap<>();

        reservedProducts = new ConcurrentHashMap<>();
        lock = new Semaphore(1,true);

        Thread reserveTimeoutThread = new Thread(this::reservationThreadMain);
        reserveTimeoutThread.start();
        isOpen = true;
    }



    public boolean openStore(){
        if(isOpen)
            return false;
        else{
            isOpen = true;
            return true;
        }
    }
    public boolean closeStore(){
        if(isOpen){
            isOpen = false;
            return true;
        }
        else
            return false;
    }
    public int calculatePrice(List<Pair<Product,Integer>> products){
        //TODO: Implement this
        return 0;
    }

    public int availableCount(String productId){
        return -1;
    }

    public String addProduct(Product toAdd) throws StoreException {
        if(isOpen) {
            if(inventory.nameExists(toAdd.productName())) {
                inventory.addProduct(toAdd);
                return "product added";
            }
            else
                return "product name exists";
        }
        else {
            throw new StoreException("store is closed");
        }
    }

    public String removeProduct(String productIdToRemove) {
        if (isOpen) {
            inventory.removeProduct(productIdToRemove);
            return "product removed";
        }
        else return "product wasnt removed - store closed";
    }

    public  boolean updateProduct(Product toUpdate){
        return inventory.updateProduct(toUpdate);
    }

    public boolean enableProduct(Product toEnable){
        return inventory.enableProduct(toEnable);
    }

    public boolean disableProduct(Product toDisable){
        return inventory.disableProduct(toDisable);
    }

    public Reservation reserveProducts(String userId, Map<Product,Integer> toReserve){

        lockAcquire();

        // Check if the user already has a reservation
        // If so, cancel it
        if(reservedProducts.containsKey(userId)){
            cancelReservation(userId);
        }

        // Check if the products are available
        for (var entry : toReserve.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            if (inventory.isProductDisabled(product) && inventory.getQuantity(product) < quantity) {
                lock.release();
                return null;
            }
        }

        // Reserve the products
        for (var entry : toReserve.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            inventory.setQuantity(product, inventory.getQuantity(product) - quantity);
        }

        // Create the reservation
        Reservation reservation = new Reservation(userId,
                new HashMap<>(){{
                    for (var entry : toReserve.entrySet()) {
                        put(entry.getKey(), entry.getValue());
                    }}},

                LocalDateTime.now().plusSeconds(reservationTimeoutSeconds), null); //TODO: Implement the cancel callback
        reservedProducts.put(userId, reservation);

        lock.release();
        synchronized (waitObject) {
            waitObject.notifyAll();
        }
        return reservation;
    }

    public void cancelReservation(String userId){
        lockAcquire();
        Reservation reservation = reservedProducts.get(userId);
        if(reservation == null){
            return;
        }
        for(var entry : reservation.productToQuantity().entrySet()){
            Product product = entry.getKey();
            int quantity = entry.getValue();
            inventory.setQuantity(product, inventory.getQuantity(product) + quantity);
        }
        reservedProducts.remove(reservation.userId());
        lock.release();
        synchronized (waitObject) {
            waitObject.notifyAll();
        }
    }

    public List<Product> searchProduct(SearchRequest request) {
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
    }

    public void setStoreRating(Rating storeRating) {
        this.storeRating = storeRating;
    }

    public Rating getStoreRating() {
        return storeRating;
    }

    public String getStoreId() {
        return storeId;
    }

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public String getStoreDescription() {
        return storeDescription;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }

    private void lockAcquire() {
        try {
            lock.acquire();
        } catch (InterruptedException ignored) {}
    }

    // ================================================================= |
    // ===================== Reservation Thread ======================== |
    // ================================================================= |

    private void reservationThreadMain(){
        long nextWakeUp = Long.MAX_VALUE;
        Reservation nextExpiringReservation = null;
        while(true){

            // Wait something to happen
            do{
                _wait(10000L);
            }while(reservedProducts.isEmpty());

            // Find the first reservation that will expire
            for(var entry : reservedProducts.entrySet()){
                long expirationTimeMillis = localDateTimeToEpochMillis(entry.getValue().expirationDate());
                if(expirationTimeMillis <= nextWakeUp){
                    nextWakeUp = expirationTimeMillis;
                    nextExpiringReservation = entry.getValue();
                }
            }

            // if for some reason there is no reservation
            // continue to the next iteration
            if(nextExpiringReservation == null){
                continue;
            }

            // Wait until the next reservation expires
            do{
                long waitTime = Math.max(nextWakeUp - System.currentTimeMillis(), 1L);
                _wait(waitTime);
            } while(System.currentTimeMillis() < nextWakeUp
                    && !nextExpiringReservation.isPaid()
                    && !nextExpiringReservation.isCancelled());

            //if the reservation is cancelled, no need to do anything
            // if not, move to the next step
            if(! nextExpiringReservation.isCancelled()){

                // if the reservation is paid, remove it
                if (nextExpiringReservation.isPaid()) {
                    reservedProducts.remove(nextExpiringReservation.userId());
                } else {

                    // if the reservation is not paid, cancel it
                    cancelReservation(nextExpiringReservation.userId());
                }
            }

            nextExpiringReservation = null;
            nextWakeUp = Long.MAX_VALUE;
        }
    }

    private void _wait(long waitTime) {
        synchronized (waitObject) {
            try {
                waitObject.wait(waitTime);
            } catch (InterruptedException ignored) {}
        }
    }

    private long localDateTimeToEpochMillis(LocalDateTime time){
        return time.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }

    public void setReservationTimeoutSeconds(long reservationTimeoutSeconds) {
        this.reservationTimeoutSeconds = reservationTimeoutSeconds;
    }

    public void addManager(String appointeeUserId, String appointedUserId) {
        if (appointedUserId != null && appointedUserId != null) {
            // add write acquire lock
            ManagementNode appointeeNode = managersList.get(appointeeUserId);
            if (appointeeNode != null) {
                if (!managersList.containsKey(appointedUserId)) {
                    ManagementNode appointedNode = appointeeNode.addManager(appointeeUserId);
                    managersList.put(appointeeUserId, appointedNode);
                }
            }
        }
    }

    public void removeManager(String appointeeUserId, String appointedUserId) {
        if (appointedUserId != null && appointedUserId != null) {
            // add write acquire lock
            ManagementNode appointeeNode = managersList.get(appointeeUserId);
            if (appointeeNode != null) {
                ManagementNode deletedManager = appointeeNode.deleteManager(appointedUserId);
                if (deletedManager != null) {
                    List<String> appointerChildren = deletedManager.getAllChildren();
                    for (String appointerToRemove : appointerChildren) {
                        managersList.remove(appointerToRemove);
                    }
                }
            }
        }
    }

    public void addOwner(String appointeeUserId, String appointedUserId) {
        if (appointedUserId != null && appointedUserId != null) {
            // add write acquire lock
            ManagementNode appointeeNode = ownershipList.get(appointeeUserId);
            if (appointeeNode != null) {
                if (!ownershipList.containsKey(appointedUserId)) {
                    ManagementNode appointedNode = appointeeNode.addManager(appointeeUserId);
                    ownershipList.put(appointeeUserId, appointedNode);
                }
            }
        }
    }

    public void removeOwner(String appointeeUserId, String appointedUserId) {
        if (appointedUserId != null && appointedUserId != null) {
            // add write acquire lock
            ManagementNode appointeeNode = ownershipList.get(appointeeUserId);
            if (appointeeNode != null) {
                ManagementNode deletedOwner = appointeeNode.deleteManager(appointedUserId);
                if (deletedOwner != null) {
                    List<String> appointerChildren = deletedOwner.getAllChildren();
                    for (String appointerToRemove : appointerChildren) {
                        ownershipList.remove(appointerToRemove);
                    }
                }
            }
        }
    }
}
