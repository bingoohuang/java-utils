package com.github.bingoohuang.utils.lang;

import com.google.common.io.ByteStreams;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Gzip {
    public static boolean isGzipStream(byte[] bytes) {
        return bytes[0] == (byte) GZIPInputStream.GZIP_MAGIC
                && bytes[1] == (byte) (GZIPInputStream.GZIP_MAGIC >>> 8);
    }

    @SneakyThrows
    public static byte[] gzip(String text) {
        return gzip(text.getBytes(StandardCharsets.UTF_8));
    }

    @SneakyThrows
    public static byte[] gzip(byte[] text) {
        val baos = new ByteArrayOutputStream();
        @Cleanup val out = new GZIPOutputStream(baos);
        out.write(text);

        return baos.toByteArray();
    }

    @SneakyThrows
    public static byte[] ungzip(byte[] bytes) {
        @Cleanup val in = new GZIPInputStream(new ByteArrayInputStream(bytes));
        val baos = new ByteArrayOutputStream();
        ByteStreams.copy(in, baos);
        return baos.toByteArray();
    }
    
}
