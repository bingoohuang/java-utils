package com.github.bingoohuang.utils.lang;

import lombok.Data;
import org.junit.Test;


public class RandTest {
    @Data
    public static class Person {
        private String name;
    }

    @Test
    public void random() {
        Person random = Rand.random(Person.class);
        System.out.println(random);
    }
}