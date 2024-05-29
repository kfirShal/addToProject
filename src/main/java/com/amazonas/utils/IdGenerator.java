package com.amazonas.utils;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class IdGenerator {

    private final AtomicLong counter;

    public IdGenerator(int initialValue) {
        counter = new AtomicLong(initialValue);
    }

    public IdGenerator() {
        this(0);
    }

    public String nextId() {
        return UUID.nameUUIDFromBytes(String.valueOf(counter.incrementAndGet()).getBytes()).toString();
    }

    public void setCounter(int value) {
        counter.set(value);
    }

}
