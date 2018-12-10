package com.github.bingoohuang.utils.lang;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import lombok.SneakyThrows;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.Serializable;

public class SerializesTest {
    static class TestSerial implements Serializable {
        public byte version = 100;
        public byte count = 0;
        public String name = "黄 进 兵";
    }

    // https://www.diffnow.com/
    @SneakyThrows
    public static void main(String[] args) {
        byte[] serialize = Serializes.serialize(new TestSerial());

        Files.write(serialize, new File("serialize1"));

        Jedis jedis = new Jedis();
        jedis.set("SerializesTest".getBytes(Charsets.UTF_8), serialize);

        System.out.println(new QuotedPrintableCodec().encode(serialize));
    }
}
