package com.github.bingoohuang.utils.codec;

import com.github.bingoohuang.utils.lang.Str;

public class Base64 {
    public static String base64(byte[] bytes) {
        return base64(bytes, Format.UrlSafe);
    }

    public static String padding(String s) {
        return Str.padding(s, '=', (4 - s.length() % 4)).toString();
    }

    public static String purify(String s) {
        return Str.removeLastLetters(s, '=').toString();
    }

    public enum Format {
        Standard,
        // URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548)
        UrlSafe,
        // 去除末尾=号
        Purified
    }

    public static String base64(String s) {
        return base64(s, Format.UrlSafe);
    }

    public static String base64(String s, Format format) {
        return base64(Bytes.bytes(s), format);
    }

    public static String base64(byte[] bytes, Format format) {
        switch (format) {
            case Standard:
                return org.apache.commons.codec.binary.Base64.encodeBase64String(bytes);
            case UrlSafe:
                return purify(org.apache.commons.codec.binary.Base64.encodeBase64URLSafeString(bytes));
            case Purified:
                return purify(org.apache.commons.codec.binary.Base64.encodeBase64String(bytes));
        }
        return null;
    }

    public static byte[] unBase64(String value) {
        return org.apache.commons.codec.binary.Base64.decodeBase64(padding(value));
    }

    public static String unBase64AsString(String value) {
        return Bytes.string(unBase64(value));
    }
}
