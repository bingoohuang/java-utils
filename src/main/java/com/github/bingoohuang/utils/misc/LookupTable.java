package com.github.bingoohuang.utils.misc;

import java.util.Objects;
import java.util.function.Supplier;

public class LookupTable {
    @SuppressWarnings("unchecked")
    public static <T> T lookupTable(Object[] table, int rowElements, Object... locateElements) {
        if (locateElements.length != rowElements - 1) throw new IllegalArgumentException("元素个数必须为" + (rowElements - 1));

        for (int i = 0; i < table.length; i += rowElements) {
            if (hit(table, i, locateElements)) {
                Object target = table[i + rowElements - 1];
                if (target instanceof Supplier) return (T) ((Supplier) target).get();

                return (T) target;
            }
        }

        throw new RuntimeException("查表失败");
    }

    public static boolean hit(Object[] table, int i, Object[] locateElements) {
        for (int j = 0; j < locateElements.length; ++j) {
            if (!Objects.equals(table[i + j], locateElements[j])) return false;
        }

        return true;
    }
}
