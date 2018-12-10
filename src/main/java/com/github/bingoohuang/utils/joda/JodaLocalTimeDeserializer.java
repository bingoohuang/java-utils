package com.github.bingoohuang.utils.joda;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import lombok.val;
import lombok.var;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;

import java.lang.reflect.Type;

public class JodaLocalTimeDeserializer implements ObjectDeserializer {
    private final String[] tryPatterns;

    public JodaLocalTimeDeserializer(String... tryPatterns) {
        this.tryPatterns = tryPatterns.length == 0 ? new String[]{"HH:mm:ss"} : tryPatterns;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LocalTime deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        Object obj = parser.parse(fieldName);
        if (obj == null) return null;

        if (obj instanceof String) {
            if (StringUtils.isBlank((String) obj)) return null;

            return parseStringLocalTime((String) obj);
        } else if (obj instanceof Number) {
            return new LocalTime(((Number) obj).longValue());
        }

        throw new IllegalArgumentException(obj + " is unknown for LocalTime");
    }

    private LocalTime parseStringLocalTime(String src) {
        IllegalArgumentException ex = null;

        for (val pattern : tryPatterns) {
            var s = src.length() > pattern.length() ? src.substring(0, pattern.length()) : src;

            try {
                val leftPattern = pattern.substring(0, s.length());
                return LocalTime.parse(s, DateTimeFormat.forPattern(leftPattern));
            } catch (IllegalArgumentException e) {
                if (ex == null) ex = e;
            }
        }

        throw ex;
    }

    @Override
    public int getFastMatchToken() {
        return 0;
    }
}
