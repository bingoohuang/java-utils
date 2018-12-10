package com.github.bingoohuang.utils.time;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Now {
    public static String now() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                .format(Calendar.getInstance().getTime());
    }

    public static String millis() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS")
                .format(Calendar.getInstance().getTime());
    }

    public static String now(String format) {
        return new SimpleDateFormat(format)
                .format(Calendar.getInstance().getTime());
    }

    public static Date date() {
        return Calendar.getInstance().getTime();
    }

}
