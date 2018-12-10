package com.github.bingoohuang.utils.str;

import org.slf4j.helpers.MessageFormatter;

public class Fmt {
    /**
     * 使用形如SLF4j的格式来生成字符串。
     *
     * @param format SLF4j格式，例如: userid is {}。
     * @param params 参数列表。
     * @return 格式完毕的字符串。
     */
    public static String format(String format, Object... params) {
        return MessageFormatter.arrayFormat(format, params).getMessage();
    }
}
