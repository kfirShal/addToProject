package com.amazonas.frontend.utils;

import java.util.function.Function;

public class IntegerToStringConverter implements Converter<Integer,String>{
    @Override
    public Function<Integer, String> to() {
        return String::valueOf;
    }

    @Override
    public Function<String, Integer> from() {
        return str -> {
            try {
                return Integer.parseInt(str);
            } catch (Exception e) {
                return null;
            }
        };
    }
}
