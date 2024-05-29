package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.exceptions.StoreException;
import com.amazonas.utils.Pair;
import com.amazonas.utils.ReadWriteLock;
import org.springframework.lang.Nullable;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Store {

    private static final int FIVE_MINUTES = 5 * 60;
    private final ReservationFactory reservationFactory;
    private final ReservationMonitor reservationMonitor;

    private final ProductInventory inventory;
    private final ConcurrentMap<String, Reservation> reservedProducts;
    private final ReadWriteLock lock;

    private String storeId;
    private String storeDescription;
    private Rating storeRating;
    private boolean isOpen;
    private long reservationTimeoutSeconds;
    private Map<String, OwnerNode> managersList;
    private OwnerNode ownershipTree;
    private Map<String, OwnerNode> ownershipList;

    public Store(String ownerUserId,
                 String storeId,
                 String description,
                 Rating rating,
                 ProductInventory inventory,
                 ReservationFactory reservationFactory,
                 ReservationMonitor reservationMonitor) {
        this.reservationFactory = reservationFactory;
        this.reservationMonitor = reservationMonitor;
        this.inventory = inventory;
        this.storeId = storeId;
        this.storeDescription = description;
        this.storeRating = rating;
        this.reservationTimeoutSeconds = FIVE_MINUTES;
        this.managersList = new HashMap<>();
        this.ownershipTree = new OwnerNode(ownerUserId, null);
        this.ownershipList = new HashMap<>();
        reservedProducts = new ConcurrentHashMap<>();
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

    //====================================================================== |
    //============================= PRODUCTS =============================== |
    //====================================================================== |

    public double calculatePrice(List<Pair<Product,Integer>> products){
        double sum = 0;
        for(Pair<Product,Integer> pair : products){
            sum += pair.first().price() * pair.second();
        }
        return sum;
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

    public int availableCount(String productId){
        return -1;
    }

    public String addProduct(Product toAdd) throws StoreException {
        if(isOpen) {
            if(inventory.nameExists(toAdd.productName())) {
                inventory.addProduct(toAdd);
                return "product added";
            }
            else {
                return "product name exists";
            }
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
        else {
            return "product wasnt removed - store closed";
        }
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

    //====================================================================== |
    //=========================== RESERVATIONS ============================= |
    //====================================================================== |

    @Nullable
    public Reservation reserveProducts(String userId, Map<Product,Integer> toReserve){
        try{
            lock.acquireWrite();

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
            Reservation reservation = reservationFactory.get(
                    storeId,
                    userId,
                    toReserve,
                    LocalDateTime.now().plusSeconds(reservationTimeoutSeconds));

            reservedProducts.put(userId, reservation);
            reservationMonitor.addReservation(reservation);

            return reservation;
        } finally {
            lock.releaseWrite();
        }
    }

    public void cancelReservation(String userId){
        try{
            lock.acquireWrite();

            Reservation reservation = reservedProducts.get(userId);
            if(reservation == null){
                return;
            }

            reservation.setCancelled();

            for(var entry : reservation.productToQuantity().entrySet()){
                Product product = entry.getKey();
                int quantity = entry.getValue();
                inventory.setQuantity(product, inventory.getQuantity(product) + quantity);
            }

            reservedProducts.remove(reservation.userId());
        } finally {
            lock.releaseWrite();
        }
    }

    public void setReservationTimeoutSeconds(long reservationTimeoutSeconds) {
        this.reservationTimeoutSeconds = reservationTimeoutSeconds;
    }


    //====================================================================== |
    //========================= STORE POSITIONS ============================ |
    //====================================================================== |

    public void addManager(String appointeeOwnerUserId, String appointedUserId) {
        if (appointedUserId != null && appointeeOwnerUserId != null) {
            // add write acquire lock
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (!managersList.containsKey(appointedUserId)) {
                    appointeeNode.addManager(appointedUserId);
                    managersList.put(appointedUserId, null);
                }
            }
        }
    }

    public void removeManager(String appointeeOwnerUserId, String appointedUserId) {
        if (appointedUserId != null && appointeeOwnerUserId != null) {
            // add write acquire lock
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (appointeeNode.deleteManager(appointedUserId)) {
                    managersList.remove(appointedUserId);
                }
            }
        }
    }

    public void addOwner(String appointeeOwnerUserId, String appointedUserId) {
        if (appointedUserId != null && appointeeOwnerUserId != null) {
            // add write acquire lock
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                if (!ownershipList.containsKey(appointedUserId)) {
                    OwnerNode appointedNode = appointeeNode.addOwner(appointedUserId);
                    ownershipList.put(appointeeOwnerUserId, appointedNode);
                }
            }
        }
    }

    public void removeOwner(String appointeeOwnerUserId, String appointedUserId) {
        if (appointedUserId != null && appointeeOwnerUserId != null) {
            // add write acquire lock
            OwnerNode appointeeNode = ownershipList.get(appointeeOwnerUserId);
            if (appointeeNode != null) {
                OwnerNode deletedOwner = appointeeNode.deleteOwner(appointedUserId);
                if (deletedOwner != null) {
                    List<String> appointerChildren = deletedOwner.getAllChildren();
                    for (String appointerToRemove : appointerChildren) {
                        ownershipList.remove(appointerToRemove);
                    }
                }
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

    public void setStoreId(String storeId) {
        this.storeId = storeId;
    }

    public void setStoreDescription(String storeDescription) {
        this.storeDescription = storeDescription;
    }
}
