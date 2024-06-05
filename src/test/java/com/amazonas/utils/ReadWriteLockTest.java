package com.amazonas.utils;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class ReadWriteLockTest {

    @Test
    void testMutualExclusion() {
        AtomicInteger writing = new AtomicInteger(0);
        AtomicInteger reading = new AtomicInteger(0);
        int n = 2;

        ReadWriteLock lock = new ReadWriteLock();
        for (int i = 0; i < n; i++) {
            new Thread(() -> {
                while(true){
                    lock.acquireRead();
                    reading.incrementAndGet();
                    if(writing.get() > 0){
                        fail("ERROR: reader reading while writer writing");
                    }
                    reading.decrementAndGet();
                    lock.releaseRead();
                }
            }).start();

            // writer threads
            new Thread(() -> {
                while(true){
                    for(long j = 0; j < 100000L; j++){}

                    // acquire the lock
                    lock.acquireWrite();
                    writing.incrementAndGet();
                    if(reading.get() > 0 || writing.get() > 1){
                        fail("ERROR: writer writing while reader reading");
                    }
                    // release the lock
                    writing.decrementAndGet();
                    lock.releaseWrite();
                }
            }).start();
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}
    }

    @Test
    void testWriterPriority(){
        AtomicInteger writing = new AtomicInteger(0);
        AtomicInteger reading = new AtomicInteger(0);
        int n = 10;

        ReadWriteLock lock = new ReadWriteLock();
        for (int i = 0; i < n; i++) {
            new Thread(() -> {
                while(true){
                    lock.acquireWrite();
                    writing.incrementAndGet();
                    lock.releaseWrite();
                }
            }).start();

            new Thread(() -> {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                while(true){
                    lock.acquireRead();
                    reading.incrementAndGet();
                    lock.releaseRead();
                }
            }).start();
        }

        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {}

        if(reading.get() > 0) fail("writers should have priority over readers");

        System.out.println("readers: " + reading.get() + " writers: " + writing.get());
    }

}