package com.github.bingoohuang.utils.lang;

import lombok.val;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

public class Collects {
    /**
     * 测试一个集合中，是否包含任一元素。
     *
     * @param collection 集合
     * @param values     测试包含元素
     * @param <T>        集合元素类型
     * @return 是否包含
     */
    public static <T> boolean containsAnyOf(Collection<T> collection, T... values) {
        for (T t : values) {
            if (collection.contains(t)) return true;
        }
        return false;
    }



    /**
     * 在一个集合中查找指定属性，是否与targetValue相等
     *
     * @param collection  集合
     * @param targetValue 目标值
     * @param getter      集合元素属性对应的getter
     * @param <T>         集合元素类型
     * @param <V>         目标值类型
     * @return true 在集合中找到了与指定属性相等
     */
    public static <T, V> boolean exists(Collection<T> collection, V targetValue, Function<T, V> getter) {
        val foundItem = find(collection, targetValue, getter);
        return foundItem.isPresent();
    }

    /**
     * 在一个集合中查找指定属性，是否与targetValue相等
     *
     * @param collection  集合
     * @param targetValue 目标值
     * @param getter      集合元素属性对应的getter
     * @param <T>         集合元素类型
     * @param <V>         目标值类型
     * @return true 在集合中找到了与指定属性相等
     */
    public static <T, V> Optional<T> find(Collection<T> collection, V targetValue, Function<T, V> getter) {
        for (T item : collection) {
            if (Objects.equals(targetValue, getter.apply(item))) {
                return Optional.of(item);
            }
        }

        return Optional.empty();
    }

    public static boolean isEmpty(Collection<?> list) {
        return list == null || list.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> list) {
        return list != null && !list.isEmpty();
    }

}
