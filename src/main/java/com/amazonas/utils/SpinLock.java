package com.amazonas.utils;

import java.util.concurrent.atomic.AtomicBoolean;

public class SpinLock {

    private final AtomicBoolean locked;

    public SpinLock(){
        locked = new AtomicBoolean(false);
    }

    /**
     * Acquire the lock non-blocking. will spin until the lock is acquired
     */
    public void acquire(){
        while(!locked.compareAndSet(false,true)){
            Thread.onSpinWait();
        }
    }

    public void release(){
        locked.set(false);
    }
}
