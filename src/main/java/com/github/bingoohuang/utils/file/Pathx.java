package com.github.bingoohuang.utils.file;

public class Pathx {
    public static String userHome = System.getProperty("user.home");
    public static String userDir = System.getProperty("user.dir");

    public static String expandUserHome(String text) {

        return text.replaceFirst("~", userHome);
    }
}
