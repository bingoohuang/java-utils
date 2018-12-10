package com.github.bingoohuang.utils.joda;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import lombok.AllArgsConstructor;
import lombok.val;
import org.joda.time.DateTime;

import java.lang.reflect.Type;

@AllArgsConstructor
public class JodaDateTimeSerializer implements ObjectSerializer {
    private final String pattern;
    private final boolean useLong;
    private final boolean nullToEmpty;

    public JodaDateTimeSerializer() {
        this("yyyy-MM-dd HH:mm:ss.SSS", false, false);
    }

    public JodaDateTimeSerializer(String pattern, boolean nullToEmpty) {
        this(pattern, false, nullToEmpty);
    }

    @Override
    public void write(JSONSerializer serializer, Object object,
                      Object fieldName, Type fieldType, int features) {
        val value = (DateTime) object;
        if (value == null) {
            if (nullToEmpty) serializer.out.writeString("");
            else serializer.out.writeNull();
        }

        if (useLong) {
            serializer.out.writeLong(value.getMillis());
        } else {
            serializer.out.writeString(value.toString(pattern));
        }
    }
}
