package com.github.bingoohuang.utils.config;

import java.util.List;

public class RefreshConfig {

    public static boolean exists(String key) {
        Config.refreshConfigSet(key);
        return Config.exists(key);
    }

    public static int getInt(String key) {
        Config.refreshConfigSet(key);
        return Config.getInt(key);
    }

    public static long getLong(String key) {
        Config.refreshConfigSet(key);
        return Config.getLong(key);
    }

    public static boolean getBool(String key) {
        Config.refreshConfigSet(key);
        return Config.getBool(key);
    }

    public static float getFloat(String key) {
        Config.refreshConfigSet(key);
        return Config.getFloat(key);
    }

    public static double getDouble(String key) {
        Config.refreshConfigSet(key);
        return Config.getDouble(key);
    }

    public static String getStr(String key) {
        Config.refreshConfigSet(key);
        return Config.getStr(key);
    }

    public static int getInt(String key, int defaultValue) {
        Config.refreshConfigSet(key);
        return Config.getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        Config.refreshConfigSet(key);
        return Config.getLong(key, defaultValue);
    }

    public static boolean getBool(String key, boolean defaultValue) {
        Config.refreshConfigSet(key);
        return Config.getBool(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue) {
        Config.refreshConfigSet(key);
        return Config.getFloat(key, defaultValue);
    }

    public static double getDouble(String key, double defaultValue) {
        Config.refreshConfigSet(key);
        return Config.getDouble(key, defaultValue);
    }

    public static String getStr(String key, String defaultValue) {
        Config.refreshConfigSet(key);
        return Config.getStr(key, defaultValue);
    }

    public static Configable subset(String prefix) {
        Config.refreshConfigSet(prefix);
        return Config.subset(prefix);
    }

    public static long refreshConfigSet(String prefix) {
        Config.refreshConfigSet(prefix);
        return Config.refreshConfigSet(prefix);
    }

    public static <T> T getBean(String key, Class<T> beanClass) {
        Config.refreshConfigSet(key);
        return Config.getBean(key, beanClass);
    }

    public static <T> List<T> getBeans(String key, Class<T> beanClass) {
        Config.refreshConfigSet(key);
        return Config.getBeans(key, beanClass);
    }
}
