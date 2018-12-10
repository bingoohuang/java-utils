package com.github.bingoohuang.utils.config.impl;

import com.github.bingoohuang.utils.config.ex.ConfigException;
import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Properties;

public class TableConfigable extends DefaultConfigable {

    public TableConfigable(Resource res) {
        super(buildProperties(res));
    }

    private static Properties buildProperties(Resource res) {
        Properties props = new Properties();

        Reader reader = null;
        try {
            reader = new InputStreamReader(res.getInputStream(), Charsets.UTF_8);
            TableReader tableReader = new TableReader(reader);
            List<ConfigTable> tables = tableReader.getTables();
            for (ConfigTable table : tables) {
                String tableName = table.getTableName();
                if (props.containsKey(tableName)) {
                    throw new ConfigException(
                            "duplicate key [" + tableName + "] in file...");
                }
                props.put(tableName, table);
            }

        } catch (IOException e) {
        } finally {
            Closeables.closeQuietly(reader);
        }
        return props;
    }

}
