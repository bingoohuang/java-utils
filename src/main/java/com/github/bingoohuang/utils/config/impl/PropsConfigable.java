package com.github.bingoohuang.utils.config.impl;

import com.github.bingoohuang.utils.config.ex.ConfigException;
import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

public class PropsConfigable extends DefaultConfigable {
    public PropsConfigable(Resource res) {
        super(buildProperties(res));
    }

    private static Properties buildProperties(Resource res) {
        PropsReader reader = null;
        Properties props = new Properties();
        try {
            reader = new PropsReader(new InputStreamReader(res.getInputStream(), Charsets.UTF_8));
            while (reader.nextProperty()) {
                if (!props.containsKey(reader.getPropertyName())) {
                    props.put(reader.getPropertyName(), reader.getPropertyValue());
                    continue;
                }

                throw new ConfigException(
                        "duplicate key in file " + res.getDescription() + " line " + reader.getLineNumber());
            }

            return props;
        } catch (IOException ex) {
            throw new ConfigException("read props file error " + res.getDescription(), ex);
        } finally {
            Closeables.closeQuietly(reader);
        }
    }

}
