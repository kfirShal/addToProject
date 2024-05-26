package com.amazonas.utils;

import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;

class ReadWriteLockTest {

    @Test
    void testLock() {
        ReadWriteLock lock = new ReadWriteLock();
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for (int i = 0; i < 10; i++) {
            executor.execute(() -> {
                lock.acquireRead();
                System.out.println("reading at "+System.currentTimeMillis());
                lock.releaseRead();
            });
            executor.execute(() -> {
                lock.acquireWrite();
                System.out.println("writing at "+System.currentTimeMillis());
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ignored) {}
                lock.releaseWrite();
            });
        }
        lock.acquireWrite();
        System.out.println("main writing at "+System.currentTimeMillis());
        lock.releaseWrite();

        while(((ThreadPoolExecutor) executor).getActiveCount() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {}
        }
    }
}