package com.amazonas.business.stores;

import com.amazonas.utils.Pair;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component("reservationMonitor")
public class ReservationMonitor {

    private final Object waitObject;

    private final ConcurrentLinkedDeque<Pair<Reservation, LocalDateTime>> reservations;

    public ReservationMonitor() {
        reservations = new ConcurrentLinkedDeque<>();
        waitObject = new Object();

        Thread reserveTimeoutThread = new Thread(this::reservationThreadMain);
        reserveTimeoutThread.start();
    }

    public void addReservation(Reservation reservation){
        reservations.add(Pair.of(reservation, reservation.expirationDate()));
        synchronized (waitObject) {
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

            // Check if the reservation is still valid
            if(r.isCancelled()){
                // remove reservation if cancelled
                reservations.removeFirst();
            } else if(r.expirationDate().isBefore(LocalDateTime.now())) {
                // cancel reservation if expired
                r.cancelReservation();
                reservations.removeFirst();
            }
        }
    }

    private long localDateTimeToEpochMillis(LocalDateTime time){
        return time.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli();
    }
}
