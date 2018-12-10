package com.github.bingoohuang.utils.conf;

import com.github.bingoohuang.utils.spring.XyzScannerRegistrar;

public class ConfScannerRegistrar extends XyzScannerRegistrar {
    @SuppressWarnings("unchecked")
    public ConfScannerRegistrar() {
        super(ConfScan.class, ConfFactoryBean.class, Conf.class);
    }
}
