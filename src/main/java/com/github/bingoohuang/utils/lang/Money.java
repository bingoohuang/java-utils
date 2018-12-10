package com.github.bingoohuang.utils.lang;


import java.text.DecimalFormat;

public class Money {
    public static String rmb(long liMoney) {
        DecimalFormat df = new DecimalFormat("0.00");
        return "￥" + df.format(liMoney / 1000.);
    }

}
