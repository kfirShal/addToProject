package com.amazonas.business.stores;

import com.amazonas.business.inventory.GlobalProductTracker;
import com.amazonas.business.inventory.Product;
import com.amazonas.business.inventory.ProductInventory;
import com.amazonas.utils.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

@Component("store")
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class Store {

    private static final int FIVE_MINUTES = 5 * 60;

    private long reservationTimeoutSeconds;
    private final GlobalProductTracker tracker;
    private final ProductInventory inventory;
    private final ConcurrentMap<String, Reservation> reservedProducts;
    private final Semaphore lock;
    private final Object waitObject = new Object();

    private Rating storeRating;

    public Store(GlobalProductTracker tracker, ProductInventory inventory) {
        this.reservationTimeoutSeconds = FIVE_MINUTES;
        this.tracker = tracker;
        this.inventory = inventory;
        reservedProducts = new ConcurrentHashMap<>();
        lock = new Semaphore(1,true);
        storeRating = Rating.NOT_RATED;

        Thread reserveTimeoutThread = new Thread(this::reservationThreadMain);
        reserveTimeoutThread.start();
    }

    public int calculateTotalPrice(List<Pair<Product,Integer>> products){
        //TODO: Implement this
        return 0;
    }

    public void addProduct(Product toAdd){
        inventory.addProduct(toAdd);
        tracker.addProduct(toAdd,this);
    }

    public  boolean removeProduct(Product toRemove){
        return inventory.removeProduct(toRemove);
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

    public boolean reserveProducts(String userId, List<Pair<Product,Integer>> toReserve){

        // Check if the user already has a reservation
        // If so, cancel it
        if(reservedProducts.containsKey(userId)){
            cancelReservation(userId);
        }

        // Acquire the lock
        lockAcquire();

        // Check if the products are available
        for (var pair : toReserve) {
            Product product = pair.first();
            int quantity = pair.second();
            if (inventory.isProductDisabled(product) && inventory.getQuantity(product) < quantity) {
                lock.release();
                return false;
            }
        }

        // Reserve the products
        for (var pair : toReserve) {
            Product product = pair.first();
            int quantity = pair.second();
            inventory.setQuantity(product, inventory.getQuantity(product) - quantity);
        }

        // Create the reservation
        Reservation reservation = new Reservation(userId,
                new HashMap<>(){{
                    for (var pair : toReserve) {
                        put(pair.first(), pair.second());
                    }}},
                LocalDateTime.now().plusSeconds(reservationTimeoutSeconds), false);
        reservedProducts.put(userId, reservation);

        lock.release();
        synchronized (waitObject) {
            waitObject.notifyAll();
        }
        return true;
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

    public boolean setReservationPaid(String userId){
        Reservation reservation = reservedProducts.get(userId);
        if(reservation == null){
            return false;
        }
        reservation.setPaid();
        return true;
    }

    public void setStoreRating(Rating storeRating) {
        this.storeRating = storeRating;
    }

    public List<Product> searchProduct(SearchRequest request) {
        return null;
    }

    public Rating getStoreRating() {
        return storeRating;
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
}
