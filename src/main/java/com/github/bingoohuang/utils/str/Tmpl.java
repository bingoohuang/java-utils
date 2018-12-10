package com.github.bingoohuang.utils.str;

import lombok.val;
import org.joor.Reflect;

import java.util.Map;
import java.util.regex.Pattern;

public class Tmpl {

    public static final Pattern VAR_PATTERN = Pattern.compile("#\\w+#");

    public static String eval(String template, Object bean) {
        int pos = 0;
        val isMap = bean instanceof Map;
        val replaced = new StringBuilder();
        val matcher = VAR_PATTERN.matcher(template);

        while (matcher.find(pos)) {
            val group = matcher.group();
            val property = group.substring(1, group.length() - 1);

            val value = isMap ? ((Map) bean).get(property) : Reflect.on(bean).field(property).get();
            replaced.append(template, pos, matcher.start()).append(value);
            pos = matcher.end();
        }

        if (pos < template.length()) {
            replaced.append(template.substring(pos));
        }

        return replaced.toString();
    }
}
