package com.amazonas.frontend.control;

public enum Endpoints {
    ENTER_AS_GUEST("userprofiles/enterasguest", String.class),
    AUTHENTICATE_USER("auth/user", String.class),
    AUTHENTICATE_GUEST("auth/guest", String.class),
    LOGIN_TO_REGISTERED("userprofiles/logintoregistered", Void.class),
    REGISTER_USER("userprofiles/register", Void.class),
    LOGOUT("userprofiles/logout", String.class),
    LOGOUT_AS_GUEST("userprofiles/logoutasguest", Void.class);


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
