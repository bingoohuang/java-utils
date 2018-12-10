package com.github.bingoohuang.utils.lang;

import org.apache.commons.lang3.RandomStringUtils;
import org.joou.ULong;

import java.security.SecureRandom;

public class Rand {

    /*
     * Thread-safe. It uses synchronization to protect the integrity of its state.
     * See SecureRandom.nextBytes with synchronized keyword.
     */
    private static final SecureRandom RANDOM = new SecureRandom();

    public static boolean randBoolean() {
        return RANDOM.nextBoolean();
    }

    public static double randDouble() {
        return RANDOM.nextDouble();
    }

    public static float randFloat() {
        return RANDOM.nextFloat();
    }

    public static int randInt() {
        return RANDOM.nextInt();
    }

    public static int randInt(int n) {
        return RANDOM.nextInt(n);
    }

    public static long randLong() {
        return RANDOM.nextLong();
    }

    public static String randNum(int count) {
        StringBuilder sb = new StringBuilder(count);
        while (sb.length() < count) {
            sb.append(ULong.valueOf(randLong()));
        }

        return sb.replace(count, sb.length(), "").toString();
    }

    public static String randAscii(int count) {
        return RandomStringUtils.random(count, 32, 127, false, false, null, RANDOM);
    }

    public static String randLetters(int count) {
        return RandomStringUtils.random(count, 0, 0, true, false, null, RANDOM);
    }

    public static String randAlphanumeric(int count) {
        return RandomStringUtils.random(count, 0, 0, true, true, null, RANDOM);
    }

    @SuppressWarnings("unchecked")
    public static <T> T random(Class<T> beanClass) {
        return (T) new ObjectRandomizer(beanClass).random();
    }
}
