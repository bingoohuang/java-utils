package com.github.bingoohuang.utils.lang;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.io.InputStream;

public class Filex {
    /**
     * 将文本写入文件。
     *
     * @param text     文本
     * @param fileName 文件名
     */
    @SneakyThrows
    public static void writeFile(String text, String fileName) {
        val file = new File(fileName);
        val sink = Files.asCharSink(file, Charsets.UTF_8);
        sink.write(text);
    }

    /**
     * 将输入流写入文件。
     *
     * @param is       输入流
     * @param fileName 文件名
     */
    @SneakyThrows
    public static void writeFile(InputStream is, String fileName) {
        val file = new File(fileName);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        Files.write(buffer, file);
    }

}
