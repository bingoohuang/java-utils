package com.github.bingoohuang.utils.lang;

import java.util.Objects;
import java.util.function.BiConsumer;

public class Iterables {
    public static <E> void forEach(Iterable<? extends E> elements, BiConsumer<Integer, ? super E> action) {
        Objects.requireNonNull(elements);
        Objects.requireNonNull(action);

        int index = 0;
        for (E element : elements) {
            action.accept(index++, element);
        }
    }
}
