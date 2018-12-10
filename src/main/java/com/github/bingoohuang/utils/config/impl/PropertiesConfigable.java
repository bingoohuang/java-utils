package com.github.bingoohuang.utils.config.impl;

import com.google.common.io.Closeables;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesConfigable extends DefaultConfigable {

    public PropertiesConfigable(Resource res) {
        super(buildProperties(res));
    }

    private static Properties buildProperties(Resource res) {
        Properties properties = new Properties();
        InputStream inputStream = null;
        try {
            inputStream = res.getInputStream();
            properties.load(inputStream);
        } catch (IOException e) {
            Closeables.closeQuietly(inputStream);
        }

        return properties;
    }

}
