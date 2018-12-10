package com.github.bingoohuang.utils.lang;

public class Nums {
    public static boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }
}
