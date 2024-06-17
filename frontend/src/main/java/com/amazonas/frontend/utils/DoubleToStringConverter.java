package com.amazonas.frontend.utils;

import java.util.function.Function;

public class DoubleToStringConverter implements Converter<Double,String>{

    @Override
    public Function<Double, String> to() {
        return String::valueOf;
    }

    @Override
    public Function<String,Double> from() {
        return str -> {
            try {
                return Double.parseDouble(str);
            } catch (Exception e) {
                return null;
            }
        };
    }
}
