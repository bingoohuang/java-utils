package com.github.bingoohuang.utils.config.ex;

public class ConfigException extends RuntimeException {

    private static final long serialVersionUID = 6278916573612646289L;

    public ConfigException(String msg) {
        super(msg);
    }

    public ConfigException(String msg, Throwable cause) {
        super(msg, cause);
    }

}
