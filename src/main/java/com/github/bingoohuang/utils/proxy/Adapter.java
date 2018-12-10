package com.github.bingoohuang.utils.proxy;

import lombok.SneakyThrows;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class Adapter {
    @SuppressWarnings("unchecked")
    public static <T> T adapt(Object obj, Class<? extends T> target) {
        return (T) (target.isAssignableFrom(obj.getClass())
                ? obj
                : target.isInterface()

                ? Proxy.newProxyInstance(target.getClassLoader(), new Class[]{target}, (p, m, args) -> adapt(obj, m, args))
                : Enhancer.create(target, new Class[]{}, (MethodInterceptor) (o, m, args, p) -> adapt(obj, m, args)));
    }

    @SneakyThrows
    public static Object adapt(Object obj, Method adapted, Object[] args) {
        return findAdapted(obj, adapted).invoke(obj, args);
    }

    @SneakyThrows
    public static Method findAdapted(Object obj, Method adapted) {
        return obj.getClass().getMethod(adapted.getName(), adapted.getParameterTypes());
    }
}
