package com.amazonas.frontend.control;

public enum Endpoints {
    EXAMPLE("example/example/example", String.class),
    LOGIN("auth", Boolean.class);

    private final String location;
    private final Class<?> clazz;

    <T> Endpoints(String location, Class<T> clazz) {
        this.location = location;
        this.clazz = clazz;
    }

    public String location() {
        return location;
    }
    
    public Class<?> clazz() {
        return clazz;
    }
}
