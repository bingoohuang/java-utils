package com.github.bingoohuang.utils.text.matcher.model;

public interface FiltersAware {
    default String getNameFilters() {
        return null;
    }

    default String getValueFilters() {
        return null;
    }
}
