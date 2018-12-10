package com.github.bingoohuang.utils.config.impl;

public class ConfigCell {

    private String cellName;
    private String cellText;

    public ConfigCell() {

    }

    public ConfigCell(String cellName, String cellText) {
        this.cellName = cellName;
        this.cellText = cellText;
    }

    public String getCellName() {
        return cellName;
    }

    public void setCellName(String cellName) {
        this.cellName = cellName;
    }

    public String getCellText() {
        return cellText;
    }

    public void setCellText(String cellText) {
        this.cellText = cellText;
    }

    @Override
    public String toString() {
        return "ConfigCell [cellName=" + cellName + ", cellText=" + cellText
                + "]";
    }

}
