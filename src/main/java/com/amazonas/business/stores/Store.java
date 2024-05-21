package com.amazonas.business.stores;

import com.amazonas.business.inventory.Product;
import com.amazonas.utils.Pair;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Semaphore;

public class Store {

    private final long reservationTimeoutSeconds;
    private Rating storeRating;

    private final ConcurrentMap<Product, Integer> productsToQuantity;
    private final ConcurrentMap<String, Product> productsToID;
    private final Set<Product> disabledProducts;

    private final ConcurrentMap<String, Reservation> reservedProducts;

    private final Semaphore lock;
    private final Object waitObject = new Object();

    public Store(Rating storeRating, long reservationTimeoutSeconds) {
        this.reservationTimeoutSeconds = reservationTimeoutSeconds;
        productsToQuantity = new ConcurrentHashMap<>();
        productsToID = new ConcurrentHashMap<>();
        disabledProducts = ConcurrentHashMap.newKeySet();
        reservedProducts = new ConcurrentHashMap<>();
        lock = new Semaphore(1,true);
        this.storeRating = storeRating;

        Thread reserveTimeoutThread = new Thread(this::reservationThreadMain);
        reserveTimeoutThread.start();
    }

    public int calculateTotalPrice(List<Pair<Product,Integer>> products){
        //TODO: Implement this
        return 0;
    }

    public boolean enableProduct(Product toEnable){
        return disabledProducts.remove(toEnable);
    }

    public  boolean updateProduct(Product toUpdate){
        Product product = productsToID.get(toUpdate.productID());
        if(product == null){
            return false;
        }
        product.changeNameProduct(toUpdate.nameProduct());
        product.changeCategory(toUpdate.category());
        product.changeRate(toUpdate.rate());
        product.changePrice(toUpdate.price());
        product.changeDescription(toUpdate.description());
        return true;
    }

    public  boolean addProduct(Product toAdd){
        if(productsToID.containsKey(toAdd.productID())){
            return false;
        }
        productsToID.put(toAdd.productID(),toAdd);
        productsToQuantity.put(toAdd,0);
        return true;
    }

    public  boolean removeProduct(Product toRemove){
        if(!productsToID.containsKey(toRemove.productID())){
            return false;
        }
        productsToID.remove(toRemove.productID());
        productsToQuantity.remove(toRemove);
        return true;
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
            if (productsToQuantity.getOrDefault(product, -1) < quantity) {
                lock.release();
                return false;
            }
        }

        // Reserve the products
        for (var pair : toReserve) {
            Product product = pair.first();
            int quantity = pair.second();
            productsToQuantity.put(product, productsToQuantity.get(product) - quantity);
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
            productsToQuantity.put(product, productsToQuantity.get(product) + quantity);
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
}
