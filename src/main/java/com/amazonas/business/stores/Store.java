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

    private Rating storeRating;

    private final ConcurrentMap<Product, Integer> productsToQuantity;
    private final ConcurrentMap<String, Product> productsToID;
    private final Set<Product> disabledProducts;

    private final ConcurrentMap<String, Reservation> reservedProducts;

    private final Semaphore lock;
    private final Object waitObject = new Object();

    public Store(Rating storeRating) {
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

    public Reservation reserveProducts(String userId, List<Pair<Product,Integer>> toReserve){

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
                return null;
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
                LocalDateTime.now().plusMinutes(10), false);
        reservedProducts.put(userId, reservation);

        lock.release();
        synchronized (waitObject) {
            waitObject.notifyAll();
        }
        return reservation;
    }

    private void lockAcquire() {
        try {
            lock.acquire();
        } catch (InterruptedException ignored) {}
    }

    public void reservationThreadMain(){
        long nextWakeUp = Long.MAX_VALUE;
        Reservation nextExpiringReservation = null;
        while(true){

            // Wait something to happen
            do{
                try {
                    waitObject.wait(10000L);
                } catch (InterruptedException ignored) {}
            }while(reservedProducts.isEmpty());

            // Find the next reservation to expire
            for(var entry : reservedProducts.entrySet()){
                Reservation reservation = entry.getValue();
                LocalDateTime expirationDate = reservation.expirationDate();
                long expirationTimeMillis = expirationDate.toEpochSecond(ZoneOffset.UTC);
                if(expirationTimeMillis < nextWakeUp){
                    nextWakeUp = expirationTimeMillis;
                    nextExpiringReservation = reservation;
                }
            }

            // Wait until the next reservation expires
            try {
                Thread.sleep(nextWakeUp - System.currentTimeMillis());
            } catch (InterruptedException ignored) {}

            assert nextExpiringReservation != null;

            // Check if the reservation is paid
            // If not, release the products
            if(! nextExpiringReservation.isPaid()){
                cancelReservation(nextExpiringReservation);
            }
            reservedProducts.remove(nextExpiringReservation.userId());

            nextExpiringReservation = null;
            nextWakeUp = Long.MAX_VALUE;
        }
    }

    public void cancelReservation(String userId){
        Reservation reservation = reservedProducts.get(userId);
        if(reservation == null){
            return;
        }
        cancelReservation(reservation);
        reservedProducts.remove(userId);
    }

    private void cancelReservation(Reservation reservation) {
        lockAcquire();
        for(var entry : reservation.productToQuantity().entrySet()){
            Product product = entry.getKey();
            int quantity = entry.getValue();
            productsToQuantity.put(product, productsToQuantity.get(product) + quantity);
        }
        lock.release();
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
}
