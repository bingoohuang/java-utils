package com.github.bingoohuang.utils.spec;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class Spec {
    @Getter @Setter private String name;
    @Getter private List<String> params = new ArrayList<String>();

    public void addParam(String param) {
        params.add(param);
    }

    @Override public String toString() {
        return "@" + name + (params.isEmpty() ? "" : "(" + String.join(", ", params) + ")");
    }
}
