package com.github.bingoohuang.utils.codec;


import lombok.val;

import java.io.ByteArrayOutputStream;

public class Base62 {
    private static char[] encodes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"
            .toCharArray();
    private static byte[] decodes = new byte[256];

    static {
        for (int i = 0; i < encodes.length; i++) {
            decodes[encodes[i]] = (byte) i;
        }
    }

    public static StringBuilder base64(byte[] data) {
        val sb = new StringBuilder(data.length * 2);
        int pos = 0, val = 0;
        for (byte aData : data) {
            val = (val << 8) | (aData & 0xFF);
            pos += 8;
            while (pos > 5) {
                sb.append(encodes[val >> (pos -= 6)]);
                val &= ((1 << pos) - 1);
            }
        }
        if (pos > 0) {
            sb.append(encodes[val << (6 - pos)]);
        }
        return sb;
    }

    public static byte[] unBase64(char[] data) {
        val baos = new ByteArrayOutputStream(data.length);
        int pos = 0, val = 0;
        for (char aData : data) {
            val = (val << 6) | decodes[aData];
            pos += 6;
            while (pos > 7) {
                baos.write(val >> (pos -= 8));
                val &= ((1 << pos) - 1);
            }
        }
        return baos.toByteArray();
    }

    public static StringBuilder base62(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length * 2);
        int pos = 0, val = 0;
        for (byte aData : data) {
            val = (val << 8) | (aData & 0xFF);
            pos += 8;
            while (pos > 5) {
                char c = encodes[val >> (pos -= 6)];
                sb.append(
                        /**/c == 'i' ? "ia" :
                                /**/c == '+' ? "ib" :
                                /**/c == '/' ? "ic" : String.valueOf(c));
                val &= ((1 << pos) - 1);
            }
        }
        if (pos > 0) {
            char c = encodes[val << (6 - pos)];
            sb.append(
                    /**/c == 'i' ? "ia" :
                            /**/c == '+' ? "ib" :
                            /**/c == '/' ? "ic" : String.valueOf(c));
        }
        return sb;
    }

    public static byte[] unBase62(char[] data) {
        val baos = new ByteArrayOutputStream(data.length);
        int pos = 0, val = 0;
        for (int i = 0; i < data.length; i++) {
            char c = data[i];
            if (c == 'i') {
                c = data[++i];
                c =
                        /**/c == 'a' ? 'i' :
                        /**/c == 'b' ? '+' :
                        /**/c == 'c' ? '/' : data[--i];
            }
            val = (val << 6) | decodes[c];
            pos += 6;
            while (pos > 7) {
                baos.write(val >> (pos -= 8));
                val &= ((1 << pos) - 1);
            }
        }
        return baos.toByteArray();
    }

}
