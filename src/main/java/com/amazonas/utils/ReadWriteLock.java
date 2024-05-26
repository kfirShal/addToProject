package com.amazonas.utils;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This lock gives priority to writers. If a writer is waiting, no readers can enter.
 * A writer will block until all readers have left.
 * If no writers are waiting, the amount of readers that can enter is unlimited.
 * @implNote This lock uses a fair semaphore in the write queue to avoid starvation.
 */
public class ReadWriteLock {

    private final AtomicInteger readers;
    private final AtomicInteger waitingToWrite;
    private final Object entryLock;
    private final Semaphore writeLock;

    public ReadWriteLock() {
        readers = new AtomicInteger();
        waitingToWrite = new AtomicInteger();
        entryLock = new Object();
        writeLock = new Semaphore(1,true);
    }

    public void acquireRead() {
        synchronized (entryLock){
            while(waitingToWrite.get() > 0){
                try{
                    entryLock.wait(100);
                } catch(InterruptedException ignored){}
            }
            increment(readers);
        }
    }

    public void releaseRead() {
        decrement(readers);
        if(readers.get() == 0){
            synchronized(entryLock){
                entryLock.notifyAll();
            }
        }
    }

    public void acquireWrite(){
        synchronized(entryLock){
            increment(waitingToWrite);
            while(readers.get() > 0){
                try{
                    entryLock.wait(100);
                } catch(InterruptedException ignored){}
            }
        }
        try {
            writeLock.acquire();
        } catch (InterruptedException ignore) {}
    }

    public void releaseWrite(){
        writeLock.release();
        decrement(waitingToWrite);
        if(waitingToWrite.get() == 0){
            synchronized(entryLock){
                entryLock.notifyAll();
            }
        }
    }

    private void increment(AtomicInteger num){
        int curr;
        do{
            curr = num.get();
        } while(!num.compareAndSet(curr, curr + 1));
    }

    private void decrement(AtomicInteger num){
        int curr;
        do{
            curr = num.get();
        } while(!num.compareAndSet(curr, curr - 1));
    }
}
