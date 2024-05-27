package com.amazonas.utils;

public class Pair<F,S> {
    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F first() {
        return first;
    }

    public void setFirst(F first) {
        this.first = first;
    }

    public S second() {
        return second;
    }

    public void setSecond(S second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }

    public static <F,S> Pair<F,S> of(F first, S second) {
        return new Pair<>(first, second);
    }
}
