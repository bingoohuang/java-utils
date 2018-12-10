package com.github.bingoohuang.utils.lang;

import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Objects;

import static org.apache.commons.lang3.StringUtils.*;

public class Str {
    public static String firstNoneEmpty(String... values) {
        for (val value : values) {
            if (StringUtils.isNotEmpty(value)) return value;
        }

        return null;
    }

    /**
     * 返回固定长度字符串（对原始字符串末尾剪切或者补齐），一般用于固定长度密码生成等。
     *
     * @param src     原始字符串
     * @param len     固定长度
     * @param padding 补齐字符
     * @return 固定长度字符串
     */
    public static String fixLen(String src, int len, char padding) {
        int slen = StringUtils.length(src);
        if (slen >= len) return src.substring(slen - len);

        StringBuilder sb = new StringBuilder();
        if (src != null) sb.append(src);
        for (int i = slen; i < len; ++i) {
            sb.append(padding);
        }

        return sb.toString();
    }

    public static <T> boolean anyOf(T value, T... strs) {
        for (T str : strs) {
            if (Objects.equals(value, str)) return true;
        }

        return false;
    }

    public static StringBuilder padding(String s, char letter, int repeats) {
        StringBuilder sb = new StringBuilder(s);
        while (repeats-- > 0) {
            sb.append(letter);
        }

        return sb;
    }

    public static StringBuilder removeLastLetters(String s, char letter) {
        StringBuilder sb = new StringBuilder(s);
        while (sb.charAt(sb.length() - 1) == letter)
            sb.deleteCharAt(sb.length() - 1);

        return sb;
    }

    // return true if 'left' and 'right' are matching parens/brackets/braces
    public static boolean matches(char left, char right) {
        if (left == '(') return right == ')';
        if (left == '[') return right == ']';
        if (left == '{') return right == '}';
        return false;
    }

    public static String substrInQuotes(String str, char left, int pos) {
        int leftTimes = 0;
        int leftPos = str.indexOf(left, pos);
        if (leftPos < 0) return "";

        for (int i = leftPos + 1; i < str.length(); ++i) {
            char charAt = str.charAt(i);
            if (charAt == left) ++leftTimes;
            else if (matches(left, charAt)) {
                if (leftTimes == 0) return str.substring(leftPos + 1, i);
                --leftTimes;
            }
        }

        return "";
    }

    public static String toStr(Object obj) {
        return obj == null ? "" : obj.toString();
    }

    public static String addLastSlash(String path) {
        if (path == null || path.isEmpty()) return path;

        return path.charAt(path.length() - 1) != File.separatorChar
                ? (path + File.separator) : path;
    }


    public static boolean anyOf(String s, String... anys) {
        for (val any : anys) {
            if (StringUtils.equals(s, any)) return true;
        }

        return false;
    }

    public static boolean anyOfIgnoreCase(String s, String... anys) {
        for (val any : anys) {
            if (StringUtils.equalsIgnoreCase(s, any)) return true;
        }

        return false;
    }

    public static boolean areAllEmpty(String... st) {
        for (val s : st) if (StringUtils.isNotEmpty(s)) return false;

        return true;
    }

    public static boolean noneOf(String s, String... anys) {
        return !anyOf(s, anys);
    }

    public static boolean startWithAny(String str, String... anys) {
        if (str == null) return false;

        for (val any : anys) if (str.indexOf(any) == 0) return true;

        return false;
    }

    public static String equalsThen(String s, String equals, String then) {
        return equals.equals(s) ? then : s;
    }

    public static boolean isEmptyOr(String s, String or) {
        return StringUtils.isEmpty(s) || s.equals(or);
    }

    public static String unquote(String str, String quote) {
        return unquote(str, quote, quote);
    }

    public static String unquote(String str, String openQuote, String closeQuote) {
        if (StringUtils.length(str) < 2) return str;

        if (str.startsWith(openQuote) && str.endsWith(closeQuote)) {
            return str.substring(1, str.length() - 1);
        }

        return str;

    }

    public static String mask(String str) {
        if (str == null) return "*";

        int len = str.length();
        if (len <= 1) return "*";
        if (len <= 2) return left(str, 1) + "*";
        if (len <= 5) return left(str, 1) + repeat('*', len - 2) + right(str, 1);
        if (len <= 9) return left(str, 2) + repeat('*', len - 4) + right(str, 2);

        return left(str, 3) + repeat('*', len - 6) + right(str, 3);
    }

    public static String nullThen(Object fv, String defaultValue) {
        return fv == null ? defaultValue : fv.toString();
    }

    /**
     * 形如oracle的decode函数。
     *
     * @param target 需要比较的值，
     * @param ifv    如果等于它，
     * @param then   那么就取本值，
     * @param more   并且以此类推。
     * @return 转换后的值
     */
    public static String decode(String target, String ifv, String then, String... more) {
        if (StringUtils.equals(target, ifv)) return then;

        for (int i = 0; i < more.length; i += 2) {
            if (i + 1 >= more.length) return more[i];

            if (StringUtils.equals(target, more[i])) return more[i + 1];
        }

        return null;
    }
}
