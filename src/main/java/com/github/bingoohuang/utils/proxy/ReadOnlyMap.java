package com.github.bingoohuang.utils.proxy;

import java.lang.reflect.Proxy;
import java.util.Map;

public interface ReadOnlyMap<K, V> {
    default boolean containsKey(K key) {
        return true;
    }

    V get(K key);

    @SuppressWarnings("unchecked")
    static <K, V> Map<K, V> proxy(ReadOnlyMap<K, V> readOnlyMap) {
        return (Map<K, V>) Proxy.newProxyInstance(ReadOnlyMap.class.getClassLoader(),
                new Class[]{Map.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "containsKey":
                            return readOnlyMap.containsKey((K) args[0]);
                        case "get":
                            return readOnlyMap.get((K) args[0]);
                    }

                    throw new RuntimeException("unsupported method " + method.getName());
                });
    }

}
