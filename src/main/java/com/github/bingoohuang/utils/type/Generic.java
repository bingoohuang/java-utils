package com.github.bingoohuang.utils.type;

import com.alibaba.fastjson.util.ParameterizedTypeImpl;
import lombok.Getter;
import lombok.val;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.LinkedHashMap;
import java.util.Map;

@Getter
public class Generic {
    private final Type genericType;
    private final boolean parameterized;
    private final ParameterizedType parameterizedType;

    public Generic(Type genericType) {
        this.genericType = genericType;
        this.parameterized = genericType instanceof ParameterizedType;
        this.parameterizedType = parameterized ? ((ParameterizedType) genericType) : null;
    }

    public static Generic of(Type genericType) {
        return new Generic(genericType);
    }

    public Class<?> getActualTypeArg(int index) {
        return parameterized ? (Class<?>) parameterizedType.getActualTypeArguments()[index] : null;
    }

    public Type[] getActualTypeArguments() {
        return parameterized ? parameterizedType.getActualTypeArguments() : null;
    }

    public Type getOwnerType() {
        return parameterized ? parameterizedType.getOwnerType() : null;
    }

    public boolean isRawType(Type type) {
        return parameterized && parameterizedType.getRawType() == type;
    }


    public static Class<?> getActualTypeArgument(Class<?> subClass, Class<?> genericInterface) {
        return getActualTypeArgument(subClass, genericInterface, 0);
    }

    public static Class<?> getActualTypeArgument(Class<?> subClass, Class<?> genericInterface, int argOrder) {
        for (val generic : subClass.getGenericInterfaces()) {
            final Generic g = Generic.of(generic);
            if (!g.isParameterized()) continue;
            if (!g.isRawType(genericInterface)) continue;

            return g.getActualTypeArg(argOrder);
        }

        Class<?> superClz = subClass.getSuperclass();
        if (superClz == Object.class) return null;

        return getActualTypeArgument(superClz, genericInterface, argOrder);
    }

    public static Type fixMapToLinkedHashMap(Type genericType) {
        final Generic g = Generic.of(genericType);
        if (!g.isParameterized()) return genericType;
        if (!g.isRawType(Map.class)) return genericType;

        return new ParameterizedTypeImpl(g.getActualTypeArguments(), g.getOwnerType(), LinkedHashMap.class);
    }


}
