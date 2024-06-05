package com.amazonas.utils;

import com.amazonas.exceptions.NullBoxException;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public class Box<T> {

    private volatile T value;

    private Box(@Nullable T value) {
        this.value = value;
    }

    @Nullable
    public T get() {
        return value;
    }

    public void set(@Nullable T value) {
        this.value = value;
    }

    public boolean isNull() {
        return value == null;
    }

    /**
     * @return the value if it is not null
     * @throws NullBoxException if the value is null
     */
    @NonNull
    public T tryGet() throws NullBoxException {
        if (value == null) throw new NullBoxException();
        return value;
    }

    public static <T> Box<T> of(@Nullable T value) {
        return new Box<>(value);
    }
}
