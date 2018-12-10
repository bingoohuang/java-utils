package com.github.bingoohuang.utils.text.matcher.model;

import com.github.bingoohuang.utils.text.matcher.AnchorAware;
import lombok.Data;

@Data
public class PatternText implements AnchorAware, FiltersAware, TempAware {
    private String pattern;
    private int index;
    private String startAnchor;
    private String endAnchor;
    private String name;
    private String temp;
    private int valueIndex;

    private String nameFilters;
    private String valueFilters;
}
