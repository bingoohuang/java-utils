package com.github.bingoohuang.utils.reflect;

import lombok.SneakyThrows;
import lombok.val;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Fields {
    /**
     * 是否需要忽略字段的处理。
     *
     * @param field          Javabean字段。
     * @param ignoredClasses 忽视的注解类。
     * @return true 需要忽略处理
     */
    public static boolean shouldIgnored(Field field, Class<? extends Annotation>... ignoredClasses) {
        if (Modifier.isStatic(field.getModifiers())) return true;
        // A synthetic field is a compiler-created field that links a local inner class
        // to a block's local variable or reference type parameter.
        // refer: https://javapapers.com/core-java/java-synthetic-class-method-field/
        if (field.isSynthetic()) return true;

        for (val ignoredClass : ignoredClasses) {
            if (field.isAnnotationPresent(ignoredClass)) return true;
        }

        // ignore un-normal fields like $jacocoData
        return field.getName().startsWith("$");
    }

    /**
     * 设置可以访问。
     *
     * @param field Javabean字段。
     */
    public static void setAccessible(Field field) {
        if (!field.isAccessible()) field.setAccessible(true);
    }

    /**
     * 获取原始字段取值。
     *
     * @param field JavaBean反射字段。
     * @param bean  字段所在的JavaBean。
     * @return 字段取值。
     */
    @SneakyThrows
    public static Object invokeField(Field field, Object bean) {
        Fields.setAccessible(field);
        return field.get(bean);
    }

    /**
     * 设置Field的值。
     *
     * @param field JavaBean的Field
     * @param bean  JavaBean
     * @param value Field的值
     */
    @SneakyThrows
    public static void setField(Field field, Object bean, Object value) {
        Fields.setAccessible(field);
        field.set(bean, value);
    }
}
