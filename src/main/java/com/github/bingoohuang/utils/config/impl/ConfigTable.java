package com.github.bingoohuang.utils.config.impl;

import java.util.ArrayList;
import java.util.List;

public class ConfigTable {

    private String tableName;
    private List<ConfigRow> rows = new ArrayList<ConfigRow>();

    public ConfigTable() {
    }

    public ConfigTable(String tableName) {
        this.tableName = tableName;
    }

    public ConfigRow getRow(int index) {
        return rows.get(index);
    }

    public ConfigRow getRow(String rowKey) {
        ConfigRow ret = null;
        for (ConfigRow row : rows) {
            if (rowKey.equals(row.getRowKey())) {
                ret = row;
                break;
            }
        }
        return ret;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<ConfigRow> getRows() {
        return rows;
    }

    public void addRow(ConfigRow row) {
        rows.add(row);
    }

    public void setRows(List<ConfigRow> rows) {
        this.rows = rows;
    }

    @Override
    public String toString() {
        return "ConfigTable [tableName=" + tableName + ", rows=" + rows + "]";
    }

}
