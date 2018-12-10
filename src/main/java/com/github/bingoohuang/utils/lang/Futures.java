package com.github.bingoohuang.utils.lang;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
public class Futures {

    @SneakyThrows
    public static <T> T futureGet(Future<T> future, long timeoutInMillis) throws TimeoutException {
        try {
            return future.get(timeoutInMillis, TimeUnit.MILLISECONDS);
        } catch (ExecutionException e) {
            log.warn("futureGet error", e);
            throw e.getCause();
        }
    }

    @SneakyThrows
    public static <T> T futureGet(Future<T> future) {
        try {
            return future.get();
        } catch (ExecutionException e) {
            log.warn("futureGet error", e);
            throw e.getCause();
        }
    }
}
