package com.github.bingoohuang.utils.codec;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import lombok.SneakyThrows;

import java.io.InputStream;

public class Bytes {
    public static byte[] bytes(String str) {
        return str == null ? null : str.getBytes(Charsets.UTF_8);
    }

    public static String string(byte[] bytes) {
        return new String(bytes, Charsets.UTF_8);
    }

    @SneakyThrows
    public static byte[] toByteArray(InputStream is) {
        return ByteStreams.toByteArray(is);
    }

    @SneakyThrows
    public static String string(InputStream is) {
        return string(toByteArray(is));
    }
}
