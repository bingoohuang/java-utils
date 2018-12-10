package com.github.bingoohuang.utils.math;

public class Doubles {
    public static String format(double value) {
        String format = String.format("%.2f", value);

        int i = format.length();
        for (; i > 0; --i) {
            char c = format.charAt(i - 1);
            if (c == '0') continue;

            if (c == '.') --i;

            break;
        }

        return i > 0 ? format.substring(0, i) : "0";
    }
}
