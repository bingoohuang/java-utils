package com.github.bingoohuang.utils.lang;

import lombok.SneakyThrows;
import lombok.Value;

import java.lang.reflect.Method;

@Value
public class MethodBean {
    private final Method method;
    private final Object object;

    @SneakyThrows
    public Object invoke(Object[] args) {
        if (!method.isAccessible()) {
            method.setAccessible(true);
        }
        return method.invoke(object, args);
    }
}
