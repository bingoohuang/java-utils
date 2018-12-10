package com.github.bingoohuang.utils.json;

import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.deserializer.ObjectDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;

public class TrimToNullStringDeserializer implements ObjectDeserializer {
    @Override public <T> T deserialze(DefaultJSONParser parser, Type type, Object fieldName) {
        final JSONLexer lexer = parser.getLexer();
        if (lexer.token() == JSONToken.LITERAL_STRING) {
            String val = lexer.stringVal();
            lexer.nextToken(JSONToken.COMMA);

            return StringUtils.isEmpty(val) ?  null : (T) val;
        }

        if (lexer.token() == JSONToken.LITERAL_INT) {
            String val = lexer.numberString();

            lexer.nextToken(JSONToken.COMMA);
            return StringUtils.isEmpty(val) ?  null : (T) val;
        }

        Object value = parser.parse();
        if (value == null) {
            return null;
        }

        return (T) value.toString();
    }

    @Override public int getFastMatchToken() {
        return JSONToken.LITERAL_STRING;
    }
}
