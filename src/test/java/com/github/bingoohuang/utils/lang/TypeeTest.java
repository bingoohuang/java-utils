package com.github.bingoohuang.utils.lang;

import com.github.bingoohuang.utils.type.Generic;
import org.junit.Ignore;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class TypeeTest {
    public interface MyInterface<T> {
        void process(T bean);
    }

    public static class MyClass implements MyInterface<String> {

        @Override
        public void process(String bean) {

        }
    }


    @Test
    public void test1() {
        Class<?> typeArgument = Generic.getActualTypeArgument(MyClass.class, MyInterface.class);
        assertThat(typeArgument).isSameAs(String.class);
    }


    public static class MyBClass extends MyClass {

        @Override
        public void process(String bean) {

        }
    }


    @Test
    public void test2() {
        Class<?> typeArgument = Generic.getActualTypeArgument(MyBClass.class, MyInterface.class);
        assertThat(typeArgument).isSameAs(String.class);
    }

    public static class MyGClass<T> implements MyInterface<T> {

        @Override
        public void process(T bean) {

        }
    }

    public static class MyCClass extends MyGClass<String> {

        @Override
        public void process(String bean) {

        }
    }


    @Test
    @Ignore
    public void test3() {
        Class<?> typeArgument = Generic.getActualTypeArgument(MyCClass.class, MyInterface.class);
        // java.lang.ClassCastException: sun.reflect.generics.reflectiveObjects.TypeVariableImpl
        // cannot be cast to java.lang.Class
        assertThat(typeArgument).isSameAs(String.class);
    }
}
