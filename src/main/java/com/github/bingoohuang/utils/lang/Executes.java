package com.github.bingoohuang.utils.lang;

import lombok.SneakyThrows;

import java.util.concurrent.Callable;

public class Executes {

    @SneakyThrows
    public static <T> T execute(Callable<T> callable) {
        return callable.call();
    }

}
