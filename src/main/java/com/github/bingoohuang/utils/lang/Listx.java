package com.github.bingoohuang.utils.lang;

import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.collect.Sets.newHashSet;

public class Listx {
    public static <T> List<T> head(List<T> list, int len) {
        return list.size() <= len ? list : list.subList(0, len);
    }

    public static <T> T head(List<T> list) {
        return list.isEmpty() ? null : list.get(0);
    }

    public static <T> T last(List<T> list) {
        return list.isEmpty() ? null : list.get(list.size() - 1);
    }

    public static <T> List<T> tail(List<T> list) {
        return list.size() > 1 ? list.subList(1, list.size() - 1) : Lists.newArrayList();
    }

    public static <T> List<T> unique(List<T> original) {
        return com.google.common.collect.Lists.newArrayList(newHashSet(original));
    }
}
