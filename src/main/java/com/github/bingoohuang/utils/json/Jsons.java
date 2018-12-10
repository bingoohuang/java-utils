package com.github.bingoohuang.utils.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.github.bingoohuang.utils.joda.JodaDateTimeDeserializer;
import com.github.bingoohuang.utils.joda.JodaDateTimeSerializer;
import com.github.bingoohuang.utils.type.Generic;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.joda.time.DateTime;

import java.lang.reflect.Field;

@Slf4j
public class Jsons {
    @SuppressWarnings("unchecked")
    public static <T> T parseJson(String json, Field field) {
        val config = new ParserConfig();
        config.putDeserializer(DateTime.class, new JodaDateTimeDeserializer());
        config.setAutoTypeSupport(true);

        val genericType = Generic.fixMapToLinkedHashMap(field.getGenericType());
        return (T) JSON.parseObject(json, genericType, config);
    }

    public static String json(Object value) {
        val config = new SerializeConfig();
        config.put(DateTime.class, new JodaDateTimeSerializer("yyyy-MM-dd HH:mm:ss.SSS", false, false));

        return JSON.toJSONString(value, config);
    }

    public static <T> T parse(String json, Class<T> tClass, boolean silent) {
        try {
            return JSON.parseObject(json, tClass);
        } catch (Exception e) {
            if (silent) {
                log.warn("parse json {} fail", json, e);
                return null;
            }
            throw new RuntimeException("parse json error " + e.getMessage() + " for " + json);
        }
    }
}

