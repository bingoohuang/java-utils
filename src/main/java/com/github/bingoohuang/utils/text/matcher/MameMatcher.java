package com.github.bingoohuang.utils.text.matcher;

import com.github.bingoohuang.utils.spec.Spec;
import com.github.bingoohuang.utils.spec.SpecParser;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
public class MameMatcher {
    private static Map<String, BiFunction<String, List<String>, Boolean>> predefinedMatchers = Maps.newHashMap();

    static {
        predefinedMatchers.put("notContains", (s, args) -> !StringUtils.contains(s, args.get(0)));
        predefinedMatchers.put("contains", (s, args) -> StringUtils.contains(s, args.get(0)));
        predefinedMatchers.put("anyOf", (s, args) -> args.contains(s));
    }

    public static boolean nameMatch(String name, String nameMatchers) {
        if (StringUtils.isEmpty(nameMatchers)) return true;

        Spec[] specs = SpecParser.parseSpecs(nameMatchers);
        for (val spec : specs) {
            String optionName = spec.getName();
            if (predefinedMatchers.containsKey(optionName)) {
                if (!predefinedMatchers.get(optionName).apply(name, spec.getParams())) return false;
            } else {
                log.warn("Unknown name matcher name @{}", optionName);
            }

        }

        return true;
    }
}
