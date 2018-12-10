package com.github.bingoohuang.utils.joda;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import lombok.val;
import org.joda.time.LocalTime;

import java.lang.reflect.Type;

public class JodaLocalTimeSerializer implements ObjectSerializer {
    private String pattern;
    private boolean nullToEmpty;

    public JodaLocalTimeSerializer(String pattern, boolean nullToEmpty) {
        this.pattern = pattern;
        this.nullToEmpty = nullToEmpty;
    }

    @Override
    public void write(JSONSerializer serializer, Object object,
                      Object fieldName, Type fieldType, int features) {
        val value = (LocalTime) object;
        if (value == null) {
            if (nullToEmpty) serializer.out.writeString("");
            else serializer.out.writeNull();
            return;
        }

        serializer.out.writeString(value.toString(pattern));
    }
}
