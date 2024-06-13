package com.amazonas.business.stores.reservations;

import com.amazonas.repository.ShoppingCartRepository;
import com.amazonas.utils.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component("reservationMonitor")
public class PendingReservationMonitor {

    private final Object waitObject;

    private final ConcurrentLinkedDeque<Pair<Reservation, LocalDateTime>> reservations;

    public PendingReservationMonitor() {
        reservations = new ConcurrentLinkedDeque<>();
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
                    if(r.isExpired()){
                        r.cancelReservation();
                        reservations.removeFirst();
                    }
                }
                case PAID, CANCELLED -> reservations.removeFirst();
            }
        }
    }

    private long localDateTimeToEpochMillis(LocalDateTime time){
        return time.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }

}
