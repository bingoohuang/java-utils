package com.github.bingoohuang.utils.lang;

import com.github.bingoohuang.utils.reflect.Fields;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.objenesis.ObjenesisStd;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Random;
import java.util.UUID;

@RequiredArgsConstructor
public class ObjectRandomizer {
    private final Class<?> beanClass;

    public Object random() {
        Object bean = new ObjenesisStd().getInstantiatorOf(beanClass).newInstance();
        for (val field : beanClass.getDeclaredFields()) {
            if (Fields.shouldIgnored(field)) continue;

            Object value = getRandomValueForField(field);
            Fields.setField(field, bean, value);
        }

        return bean;
    }

    private Object getRandomValueForField(Field field) {
        Random random = new Random();
        Class<?> type = field.getType();

        // Note that we must handle the different types here! This is just an
        // example, so this list is not complete! Adapt this to your needs!
        if (type.isEnum()) {
            Object[] enumValues = type.getEnumConstants();
            return enumValues[random.nextInt(enumValues.length)];
        } else if (type.equals(Integer.TYPE) || type.equals(Integer.class)) {
            return random.nextInt();
        } else if (type.equals(Long.TYPE) || type.equals(Long.class)) {
            return random.nextLong();
        } else if (type.equals(Double.TYPE) || type.equals(Double.class)) {
            return random.nextDouble();
        } else if (type.equals(Float.TYPE) || type.equals(Float.class)) {
            return random.nextFloat();
        } else if (type.equals(String.class)) {
            return UUID.randomUUID().toString();
        } else if (type.equals(BigInteger.class)) {
            return BigInteger.valueOf(random.nextInt());
        }

        return new ObjectRandomizer(type).random();
    }
}
