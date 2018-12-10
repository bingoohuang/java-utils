package com.github.bingoohuang.utils.lang;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Arr {
    public static <T, V> Optional<T> find(T[] collection, V targetValue, Function<T, V> getter) {
        for (T item : collection) {
            if (Objects.equals(targetValue, getter.apply(item))) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

}
