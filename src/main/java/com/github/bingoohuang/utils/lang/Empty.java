package com.github.bingoohuang.utils.lang;

import java.util.Collection;
import java.util.Map;

public class Empty {

    public static boolean isEmpty(Object obj) {
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        } else if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        } else if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        } else {
            return obj == null;
        }
    }
}
