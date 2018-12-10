package com.github.bingoohuang.utils.text.matcher;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

@Builder @RequiredArgsConstructor
public class TextMatcherOption {
    private final String stripChars;
    private final String startAnchor;
    private final String endAnchor;

    public TextMatcherOption(String stripChars) {
        this(stripChars, null, null);
    }

    public TextMatcherOption(String startAnchor, String endAnchor) {
        this(null, startAnchor, endAnchor);
    }

    public Pair<Integer, Integer> locateRange(String text) {
        int start = StringUtils.isNotEmpty(startAnchor) ? text.indexOf(startAnchor) : 0;
        start = start <= 0 ? 0 : start + startAnchor.length();

        int end = text.length();
        if (StringUtils.isNotEmpty(endAnchor)) {
            int close = text.indexOf(endAnchor, start);
            if (close >= 0) end = close;
        }

        return Pair.of(start, end);
    }

    public String strip(String text) {
        return StringUtils.trim(StringUtils.strip(StringUtils.trim(text), stripChars));
    }
}
