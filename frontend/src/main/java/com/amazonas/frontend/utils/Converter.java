package com.amazonas.frontend.utils;

import java.util.function.Function;

public interface Converter<T1,T2> {

    Class<T1> fromType();

    Class<T2> toType();

    Function<T1, T2> to();

    Function<T2, T1> from();
}