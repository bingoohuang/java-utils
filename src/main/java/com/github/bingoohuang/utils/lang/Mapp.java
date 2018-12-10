package com.github.bingoohuang.utils.lang;

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.bingoohuang.utils.codec.Json.json;
import static com.github.bingoohuang.utils.codec.Json.unJson;

public class Mapp {
    /**
     * 将形如{"k1:v2","k2:v2"}的字符串数组，转换为Map.
     *
     * @param sep           分隔符
     * @param keyValuePairs 键值对
     * @return Map
     */
    public static Map<String, String> createMap(String sep, String... keyValuePairs) {
        Map<String, String> map = Maps.newHashMap();
        for (val keyValue : keyValuePairs) {
            int sepPos = keyValue.indexOf(sep);
            if (sepPos < 0) continue;

            val k = StringUtils.trim(keyValue.substring(0, sepPos));
            val v = StringUtils.trim(keyValue.substring(sepPos + sep.length()));
            map.put(k, v);
        }

        return map;
    }

    /**
     * 从map中按照key列表提取第一个非null的值。
     *
     * @param map  Map
     * @param keys key列表
     * @param <K>  the type of keys maintained by this map
     * @param <V>  the type of mapped values
     * @return 第一个非null的值。如果没有找到，返回null。
     */
    public static <K, V> V firstNonNull(Map<K, V> map, String... keys) {
        for (val key : keys) {
            V v = map.get(key);
            if (v != null) return v;
        }

        return null;
    }

    public static <K, V> Map<K, V> of(K k1, V v1) {
        Map<K, V> map = newHashMap();
        map.put(k1, v1);

        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2) {
        Map<K, V> map = of(k1, v1);
        map.put(k2, v2);

        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        Map<K, V> map = of(k1, v1, k2, v2);
        map.put(k3, v3);

        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        Map<K, V> map = of(k1, v1, k2, v2, k3, v3);
        map.put(k4, v4);

        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
        Map<K, V> map = of(k1, v1, k2, v2, k3, v3, k4, v4);
        map.put(k5, v5);

        return map;
    }

    public static <K, V> Map<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5, K k6, V v6) {
        Map<K, V> map = of(k1, v1, k2, v2, k3, v3, k4, v4, k5, v5);
        map.put(k6, v6);

        return map;
    }

    public static <T> Map<T, T> of(T... keyAndValues) {
        Map<T, T> map = newHashMap();
        for (int i = 0; i < keyAndValues.length; i += 2) {
            T key = keyAndValues[i];
            T value = i + 1 < keyAndValues.length ? keyAndValues[i + 1] : null;
            map.put(key, value);
        }

        return map;
    }

    public static Map<Object, Object> map(Object... keyAndValues) {
        Map<Object, Object> map = newHashMap();
        for (int i = 0; i < keyAndValues.length; i += 2) {
            Object key = keyAndValues[i];
            Object value = i + 1 < keyAndValues.length ? keyAndValues[i + 1] : null;
            map.put(key, value);
        }

        return map;
    }

    public static <K, V> Map<K, V> newHashMap() {
        return new HashMap<K, V>();
    }

    public static boolean isEmpty(Map<?, ?> map) {
        return map == null || map.isEmpty();
    }

    public static String getStr(Map m, Object key) {
        return getStr(m, key, null);
    }

    public static String getStr(Map m, Object key, String defaultValue) {
        if (m == null) return defaultValue;
        Object value = m.get(key);
        if (value == null) return defaultValue;
        return value.toString();
    }

    @SneakyThrows
    public static Number getNum(Map m, Object key) {
        if (m == null) return null;
        Object value = m.get(key);
        if (value == null) return null;
        if (value instanceof Number) return (Number) value;
        if (!(value instanceof String)) return null;

        return NumberFormat.getInstance().parse((String) value);
    }

    public static Integer getInt(Map m, Object key) {
        Number value = getNum(m, key);
        if (value == null) return null;
        return value instanceof Integer ? (Integer) value : new Integer(value.intValue());
    }

    public static Long getLong(Map m, Object key) {
        Number value = getNum(m, key);
        if (value == null) return null;
        return value instanceof Long ? (Long) value : new Long(value.longValue());
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> desc(Object obj) {
        return unJson(json(obj), Map.class);
    }

    @SuppressWarnings("unchecked")
    public static <T> T spec(Map map, Class<T> clz) {
        return unJson(json(map), clz);
    }

    @SuppressWarnings("unchecked")
    public static Map mapFromList(List<Map> list, String keyKey, String valueKey) {
        if (list == null) return newHashMap();

        Map result = newHashMap();
        for (Map map : list) {
            if (!map.containsKey(keyKey) || StringUtils.isEmpty(getStr(map, keyKey)) ||
                    !map.containsKey(valueKey) || StringUtils.isEmpty(getStr(map, valueKey))) continue;
            result.put(map.get(keyKey), map.get(valueKey));
        }
        return result;
    }

}
