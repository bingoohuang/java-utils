package com.github.bingoohuang.utils.text.matcher;

import com.github.bingoohuang.utils.filter.Filters;
import com.github.bingoohuang.utils.math.Mathx;
import com.github.bingoohuang.utils.time.DateTimeRegular;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Pattern;

@Slf4j
public class Filter {
    private static Map<String, BiFunction<String, List<String>, String>> predefinedFilters = Maps.newHashMap();
    private static Pattern TRAILING_ZERO_PATTERN = Pattern.compile("(\\.\\d*?)0+\\b");

    static {
        // 压缩字符串中的多个空格为一个。
        predefinedFilters.put("compactBlanks", (s, args) -> s.replaceAll("\\s\\s+", " "));

        // 去除数字类型小数后面多余的0，例如：4.7000->4.7, 4.00->4
        predefinedFilters.put("trimTrailingZeroes", (s, args) -> {
            val m = TRAILING_ZERO_PATTERN.matcher(s);
            return m.find() ? m.replaceAll(m.group(1).equals(".") ? "" : m.group(1)) : s;
        });

        // 归整化日期时间，例如：2018-10-24T11:21:11.683-> 2018-10-24 11:21:11
        predefinedFilters.put("regularDateTime", (s, args) -> new DateTimeRegular(s).regular()
                .toString(args.isEmpty() ? "yyyy-MM-dd HH:mm:ss" : args.get(0)));

        // 去除指定前缀
        predefinedFilters.put("unPrefix", (s, args) -> {
            String s2 = s;
            for (val arg : args) {
                if (StringUtils.startsWith(s2, arg)) {
                    s2 = s2.substring(arg.length());
                }
            }

            return s2;
        });

        // 去除指定后缀
        predefinedFilters.put("unPostfix", (s, args) -> {
            String s2 = s;
            for (val arg : args) {
                if (StringUtils.endsWith(s2, arg)) {
                    s2 = s2.substring(0, s2.length() - arg.length());
                }
            }

            return s2;
        });

        // 映射处理
        predefinedFilters.put("map", (s, args) ->
                args.size() == 2 && StringUtils.equals(s, args.get(0)) ? args.get(1) : s);

        // 浮点数规整处理
        predefinedFilters.put("roundup", (s, args) ->
                Mathx.roundHalfUp(s, args.isEmpty() ? 1 : Integer.parseInt(args.get(0))));
    }

    public static String filter(String s, String filersOptions) {
        return Filters.filter(predefinedFilters, s, filersOptions);
    }

}
