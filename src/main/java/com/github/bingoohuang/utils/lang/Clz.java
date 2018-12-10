package com.github.bingoohuang.utils.lang;

import lombok.SneakyThrows;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class Clz {
    public static boolean classExists(String className) {
        try {
            Class.forName(className, false, Clz.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SneakyThrows @SuppressWarnings("unchecked")
    public static <T> T newInstance(String className) {
        return (T) Clz.forName(className).newInstance();
    }


    public static boolean isAssignable(Class<?> fromClass, Class<?>... toClasses) {
        for (Class<?> toClass : toClasses)
            if (ClassUtils.isAssignable(fromClass, toClass)) return true;

        return false;
    }

    public static boolean isConcrete(Class<?> clazz) {
        return !clazz.isInterface() && !Modifier.isAbstract(clazz.getModifiers());
    }

    public static Class<?> findClass(String className) {
        if (StringUtils.isBlank(className)) return null;

        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            // Ignore
        }

        return null;
    }

    /**
     * Load a class given its name. BL: We wan't to use a known ClassLoader--hopefully the heirarchy is set correctly.
     *
     * @param <T>       类型
     * @param className A class name
     * @return The class pointed to by <code>className</code>
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<T> loadClass(String className) {
        if (StringUtils.isBlank(className)) return null;

        try {
            return (Class<T>) getClassLoader().loadClass(className);
        } catch (ClassNotFoundException e) {
            // Ignore
        }
        return null;
    }


    @SneakyThrows
    public static Class forName(String className) {
        return Class.forName(className);
    }


    /**
     * Return the context classloader. BL: if this is command line operation, the classloading issues are more sane.
     * During servlet execution, we explicitly set the ClassLoader.
     *
     * @return The context classloader.
     */
    public static ClassLoader getClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * Get method.
     *
     * @param class1     class
     * @param methodName method name
     * @return method
     */
    public static Method getMethod(Class<? extends Object> class1, String methodName) {
        try {
            return class1.getMethod(methodName);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 安静的调用对象的方法。
     *
     * @param target 对象
     * @param m      方法
     * @return 方法返回
     */
    public static Object invokeQuietly(Object target, Method m) {
        try {
            return m.invoke(target);
        } catch (IllegalArgumentException e) {
        } catch (IllegalAccessException e) {
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            if (e.getTargetException() instanceof RuntimeException) {
                throw (RuntimeException) e.getTargetException();
            }
        }

        return null;
    }

}
