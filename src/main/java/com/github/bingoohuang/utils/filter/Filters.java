package com.github.bingoohuang.utils.filter;

import com.github.bingoohuang.utils.spec.SpecParser;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Slf4j
public class Filters {
    public static <T> T filter(Map<String, BiFunction<T, List<String>, T>> predefinedFilters,
                               T value, String filtersOptions) {
        if (StringUtils.isEmpty(filtersOptions)) return value;

        T old = value;
        T filtered = value;
        val specs = SpecParser.parseSpecs(filtersOptions);

        for (val spec : specs) {
            val func = predefinedFilters.get(spec.getName());
            if (func != null) {
                filtered = func.apply(old, spec.getParams());
                log.debug("user {} to filter {} with result {}", spec, old, filtered);
                old = filtered;
            } else {
                log.warn("Unknown filter name @{}", spec.getName());
            }
        }

        return filtered;
    }
}
