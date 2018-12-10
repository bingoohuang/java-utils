package com.github.bingoohuang.utils.lang;

public interface Os {
    String name = System.getProperty("os.name").toLowerCase();
    boolean isWindows = name.startsWith("windows");
}
