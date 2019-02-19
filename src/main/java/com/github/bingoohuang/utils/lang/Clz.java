package com.github.bingoohuang.utils.lang;

import lombok.SneakyThrows;
import lombok.val;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

public class Clz {
    public static boolean classExists(String className) {
        try {
            Class.forName(className, false, Clz.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
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


    /**
     * Retrieves all interfaces implemented by a specified interface
     * including all recursively extended interfaces and the classes supplied
     * int the parameter.
     *
     * @param child child class
     * @return Class[] an array of interfaces that includes those specifed
     * in childInterfaces plus all of those interfaces' super interfaces
     */
    public static Set<Class> getAllInterfaces(Class child) {
        return getAllInterfaces(child.getInterfaces());
    }

    /**
     * Retrieves all interfaces implemented by a specified interface
     * including all recursively extended interfaces and the classes supplied
     * int the parameter.
     *
     * @param childInterfaces a set of interfaces
     * @return Class[] an array of interfaces that includes those specifed
     * in childInterfaces plus all of those interfaces' super interfaces
     */
    public static Set<Class> getAllInterfaces(Class[] childInterfaces) {
        Set<Class> all = new HashSet<>();

        for (int i = 0; i < childInterfaces.length; i++) {
            Class childInterface = childInterfaces[i];
            if (all.contains(childInterface)) continue;

            all.add(childInterface);
            all.addAll(getAllInterfaces(childInterface.getInterfaces()));
        }

        return all;
    }


    public static Optional<MethodBean> findMethod(Method method, Object... objs) {
        for (val obj : objs) {
            val m = findClassMethod(obj.getClass(), method);
            if (m.isPresent()) return Optional.of(new MethodBean(m.get(), obj));
        }

        return Optional.empty();
    }

    private static Optional<Method> findClassMethod(Class<?> clazz, Method method) {
        MC:
        for (val m : clazz.getMethods()) {
            if (!m.getName().equals(method.getName())) {
                continue;
            }

            if (m.getParameterCount() != method.getParameterCount()) {
                continue;
            }

            val aTypes = m.getParameterTypes();
            val bTypes = method.getParameterTypes();
            for (int i = 0, ii = m.getParameterCount(); i < ii; ++i) {
                if (aTypes[i] != bTypes[i]) {
                    continue MC;
                }
            }

            return Optional.of(m);

        }
        return Optional.empty();
    }
}
