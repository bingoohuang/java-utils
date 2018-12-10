package com.github.bingoohuang.utils.lang;

import lombok.SneakyThrows;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Threadx {
    private final static ThreadLocalRandom random = ThreadLocalRandom.current();

    /**
     * Sleep in random time between minMillis and maxMillis.
     *
     * @param minMillis minimum time in millis
     * @param maxMillis maximum time in millis
     * @return true if interrupted
     */
    public static boolean randomSleepMillis(int minMillis, int maxMillis) {
        try {
            Thread.sleep(random.nextInt(maxMillis - minMillis) + minMillis);
            return false;
        } catch (InterruptedException e) {
            return true;
        }
    }

    @SneakyThrows
    public static void sleepMillis(long milis) {
        Thread.sleep(milis);
    }


    public static void sleepMillisSilently(long milis) {
        try {
            Thread.sleep(milis);
        } catch (InterruptedException ignore) {
        }
    }


    public static void sleep(long time, TimeUnit timeUnit) {
        sleepMillis(timeUnit.toMillis(time));
    }
}
