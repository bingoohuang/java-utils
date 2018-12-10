package com.github.bingoohuang.utils.math;

import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class Mathx {
    /**
     * 四舍五入到指定小数位数。
     *
     * @param value 数值字符串
     * @param scale 指定小数位数
     * @return 四舍五入后的结果
     */
    public static String roundHalfUp(String value, int scale) {
        try {
            return new BigDecimal(value).setScale(scale, BigDecimal.ROUND_HALF_UP).toString();
        } catch (NumberFormatException e) {
            log.warn("bad number format for {}", value);
            return value;
        }
    }
}
