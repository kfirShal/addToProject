package com.amazonas.business.stores.reservations;

import com.amazonas.utils.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component("reservationMonitor")
public class ReservationMonitor {

    private final Object waitObject;

    private final ConcurrentLinkedDeque<Pair<Reservation, LocalDateTime>> reservations;
    private final Set<Reservation> paidReservations;

    public ReservationMonitor() {
        reservations = new ConcurrentLinkedDeque<>();
        paidReservations = ConcurrentHashMap.newKeySet();
        waitObject = new Object();

        Thread reserveTimeoutThread = new Thread(this::reservationThreadMain);
        reserveTimeoutThread.start();
    }

    public void addReservation(Reservation reservation){
        synchronized (waitObject) {
            reservations.add(Pair.of(reservation, reservation.expirationDate()));
            waitObject.notify();
        }
    }

    //TODO: alert admin of reservations that are paid but not shipped
    private void alertAdminShippingIssue(Reservation r){

    }

    // ================================================================= |
    // ===================== Reservation Thread ======================== |
    // ================================================================= |

    private void reservationThreadMain(){
        while(true){

            // Wait something to happen
            synchronized (waitObject) {
                if(reservations.isEmpty()){
                    try {
                        waitObject.wait();
                    } catch (InterruptedException ignored) {}
                }
            }

            Reservation r;
            try{
                r = reservations.getFirst().first();
            } catch (NoSuchElementException ignored){
                continue; // realistically, this should never happen
            }

            // Check if the reservation is still valid
            if(r.isCancelled()){
                reservations.removeFirst();
                continue;
            }

            // Wait until the reservation expires
            long waitTime = localDateTimeToEpochMillis(r.expirationDate()) - System.currentTimeMillis();
            synchronized (waitObject) {
                try {
                    waitObject.wait(waitTime);
                } catch (InterruptedException ignored) {}
            }

            switch(r.state()){
                case PENDING -> {
                    if(r.expirationDate().isBefore(LocalDateTime.now())){
                        r.cancelReservation();
                        reservations.removeFirst();
                    }
                }
                case PAID -> {
                    reservations.removeFirst();
                    paidReservations.add(r);
                }
                case SHIPPED -> paidReservations.remove(r);
                case CANCELLED -> reservations.removeFirst();
            }

            paidReservations.stream().filter(Reservation::isShipped).forEach(this::alertAdminShippingIssue);
        }
    }

    private long localDateTimeToEpochMillis(LocalDateTime time){
        return time.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }

}
