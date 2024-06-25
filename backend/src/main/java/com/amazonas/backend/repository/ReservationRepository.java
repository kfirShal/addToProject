package com.amazonas.backend.repository;

import com.amazonas.backend.business.stores.reservations.Reservation;
import com.amazonas.common.utils.ReadWriteLock;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository("reservationRepository")
public class ReservationRepository{


    private final Map<String, List<Reservation>> reservationCache;
    private final ReadWriteLock reservationLock;

    public ReservationRepository() {
        reservationCache = new HashMap<>();
        reservationLock = new ReadWriteLock();
    }

    public void saveReservation(String userId, Reservation reservation) {
        reservationLock.acquireWrite();
        try {
            reservationCache.computeIfAbsent(userId, _ -> new LinkedList<>()).add(reservation);
        } finally {
            reservationLock.releaseWrite();
        }
    }

    public List<Reservation> getReservations(String userId){
        reservationLock.acquireRead();
        try {
            return reservationCache.getOrDefault(userId, Collections.emptyList());
        } finally {
            reservationLock.releaseRead();
        }
    }

    public void removeReservation(String userId, Reservation reservation) {
        reservationLock.acquireWrite();
        try {
            reservationCache.getOrDefault(userId, Collections.emptyList()).remove(reservation);
            if(reservationCache.get(userId).isEmpty()){
                reservationCache.remove(userId);
            }
        } finally {
            reservationLock.releaseWrite();
        }
    }

}