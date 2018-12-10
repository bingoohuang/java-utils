package com.github.bingoohuang.utils.conf;

import java.lang.annotation.*;

/**
 * 配置注解。
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Conf {
    /**
     * 配置来源。
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.TYPE}) @interface Source {
        String[] value();
    }


    /**
     * 配置的KEY。
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD}) @interface Key {
        String value();
    }

    /**
     * 配置的默认值。
     */
    @Documented
    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.METHOD}) @interface DefaultValue {
        String value();
    }

}
