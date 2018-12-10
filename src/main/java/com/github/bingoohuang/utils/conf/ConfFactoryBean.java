package com.github.bingoohuang.utils.conf;

import com.github.bingoohuang.utils.spring.XyzFactoryBean;

public class ConfFactoryBean extends XyzFactoryBean {
    public ConfFactoryBean() {
        super(ConfFactory::create);
    }
}
