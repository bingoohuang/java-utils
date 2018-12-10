package com.github.bingoohuang.utils.spring;

import com.github.bingoohuang.utils.lang.Clz;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import org.n3r.eql.eqler.generators.ActiveProfilesThreadLocal;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.function.Function;

@RequiredArgsConstructor
public class XyzFactoryBean implements FactoryBean, ApplicationContextAware {
    private final Function<Class, Object> factory;

    @Setter private Class xyzInterface;
    @Setter private ApplicationContext applicationContext;


    @Override
    public Object getObject() {
        val activeProfiles = applicationContext.getEnvironment().getActiveProfiles();
        if (Clz.classExists("org.n3r.eql.eqler.generators.ActiveProfilesThreadLocal")) {
            ActiveProfilesThreadLocal.set(activeProfiles);
        }
        return factory.apply(xyzInterface);
    }

    @Override
    public Class<?> getObjectType() {
        return this.xyzInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
