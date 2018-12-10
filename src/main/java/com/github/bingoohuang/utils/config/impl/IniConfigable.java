package com.github.bingoohuang.utils.config.impl;

import com.github.bingoohuang.utils.config.ex.ConfigException;
import com.google.common.base.Charsets;
import com.google.common.io.Closeables;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;
import java.util.Properties;

public class IniConfigable extends DefaultConfigable {

    public IniConfigable(Resource res) {
        super(buildProperties(res));
    }

    private static Properties buildProperties(Resource res) {
        Reader reader = null;
        Properties props = new Properties();
        try {
            reader = new InputStreamReader(res.getInputStream(), Charsets.UTF_8);
            IniReader iniReader = new IniReader(reader);
            for (String section : iniReader.getSections()) {
                Properties sectionProps = iniReader.getSection(section);
                if (sectionProps == null) continue;

                String prefix = section.equals("") ? "" : section + '.';
                for (Map.Entry<Object, Object> entry : sectionProps.entrySet()) {
                    String key = prefix + entry.getKey();

                    if (!props.containsKey(key)) {
                        props.put(key, entry.getValue().toString());
                        continue;
                    }

                    throw new ConfigException(
                            "duplicate key in file " + res
                                    + " line " + iniReader.getLineNumber());
                }
            }
        } catch (IOException ex) {
            throw new ConfigException("read ini file error " + res, ex);
        } finally {
            Closeables.closeQuietly(reader);
        }
        return props;
    }

}
