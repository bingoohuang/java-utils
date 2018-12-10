package com.github.bingoohuang.utils.text.matcher.model;

public interface TempAware {
    default String getTemp() {
        return "";
    }
}
