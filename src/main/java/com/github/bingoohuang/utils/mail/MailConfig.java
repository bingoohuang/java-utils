package com.github.bingoohuang.utils.mail;

import com.github.bingoohuang.utils.lang.Classpath;

import java.util.Properties;

public interface MailConfig {
    Properties env = Classpath.loadProperties("mail-config.properties");

    static String get(String name) {
        return env.getProperty(name);
    }

    static Properties getEnv() {
        return env;
    }
}
