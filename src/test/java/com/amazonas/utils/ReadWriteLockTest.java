package com.amazonas.utils;

import org.junit.jupiter.api.Test;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ReadWriteLockTest {

    @Test
    void testLock() {
        AtomicBoolean writing = new AtomicBoolean(false);
        AtomicInteger reading = new AtomicInteger(0);
        int n = 50;
        AtomicInteger threadsRunning = new AtomicInteger(2*n);


        Random rand =  new Random();
        ReadWriteLock lock = new ReadWriteLock();
        for (int i = 0; i < n; i++) {

            // reader threads
            new Thread(() -> {
                try {
                    Thread.sleep(rand.nextInt(1,10000));
                } catch (InterruptedException ignored) {}
                lock.acquireRead();
                reading.incrementAndGet();
                if(writing.get()){
                    fail("ERROR: reader reading while writer writing");
                }
                for(long j = 0; j < 900000000L; j++){}
                reading.decrementAndGet();
                lock.releaseRead();
                threadsRunning.decrementAndGet();
            }).start();

            // writer threads
            new Thread(() -> {

                // sleep for a random amount of time
                try {
                    Thread.sleep(rand.nextInt(1,10000));
                } catch (InterruptedException ignored) {}

                // acquire the lock
                lock.acquireWrite();
                writing.set(true);
                if(reading.get() > 0){
                    fail("ERROR: writer writing while reader reading");
                }

                // do some work
                for(long j = 0; j < 99000000L; j++){}

                // release the lock
                writing.set(false);
                lock.releaseWrite();

                threadsRunning.decrementAndGet();
            }).start();
        }

        while(threadsRunning.get() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }
}