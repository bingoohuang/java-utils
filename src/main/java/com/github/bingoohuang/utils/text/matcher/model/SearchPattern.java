package com.github.bingoohuang.utils.text.matcher.model;

import com.github.bingoohuang.utils.text.matcher.AnchorAware;
import lombok.Data;

@Data
public class SearchPattern implements AnchorAware, FiltersAware, TempAware {
    private String pattern;
    private String startAnchor;
    private String endAnchor;
    private int nameIndex;
    private int descIndex;
    private int valueIndex;
    private String temp;

    private String nameFilters;
    private String valueFilters;

    private String nameMatchers;
}
