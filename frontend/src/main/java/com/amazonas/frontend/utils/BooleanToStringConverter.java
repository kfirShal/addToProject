package com.amazonas.frontend.utils;

import java.util.function.Function;

public class BooleanToStringConverter implements Converter<Boolean,String>{
    @Override
    public Function<Boolean, String> to() {
        return String::valueOf;
    }

    @Override
    public Function<String, Boolean> from() {
        return str -> {
            try {
                return Boolean.parseBoolean(str);
            } catch (Exception e) {
                return null;
            }
        };
    }
}
