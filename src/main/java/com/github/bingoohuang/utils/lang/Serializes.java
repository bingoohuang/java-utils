package com.github.bingoohuang.utils.lang;

import com.google.common.base.Optional;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializes {
    @SneakyThrows
    public static byte[] serialize(Object object) {
        @Cleanup val baos = new ByteArrayOutputStream();
        @Cleanup val oos = new ObjectOutputStream(baos);
        oos.writeObject(object);
        return baos.toByteArray();
    }

    @SneakyThrows
    public static Object deserialize(byte[] bytes) {
        @Cleanup val bais = new ByteArrayInputStream(bytes);
        @Cleanup val ois = new ObjectInputStream(bais);
        return ois.readObject();
    }

    @SneakyThrows
    public static Optional<Object> tryDeserialize(byte[] bytes) {
        try {
            @Cleanup val bais = new ByteArrayInputStream(bytes);
            @Cleanup val ois = new ObjectInputStream(bais);
            return Optional.of(ois.readObject());

        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Exception e) {
            return Optional.absent();
        }
    }
}
