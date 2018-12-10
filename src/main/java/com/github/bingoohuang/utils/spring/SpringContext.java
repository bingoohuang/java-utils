package com.github.bingoohuang.utils.spring;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

public abstract class SpringContext implements ApplicationContextAware {
    private static ApplicationContext appContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.appContext = applicationContext;
    }

    public static ApplicationContext getAppContext() {
        return appContext;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanName) {
        return (T) appContext.getBean(StringUtils.uncapitalize(beanName));
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBeanOrNull(String beanName) {
        if (appContext == null) return null;
        if (StringUtils.isEmpty(beanName)) return null;

        try {
            return (T) appContext.getBean(beanName);
        } catch (NoSuchBeanDefinitionException e) {
            // ignore
        }

        return null;
    }


    public static <T> T getBean(Class<T> clazz) {
        return appContext.getBean(clazz);
    }

    public static <T> T getBeanOrNull(Class<T> clazz) {
        if (appContext == null) return null;

        try {
            return appContext.getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            // ignore
        }

        return null;
    }

    public static <T> T getBeanExactly(Class<T> clazz) {
        Map<String, T> beansOfType = appContext.getBeansOfType(clazz);
        if (beansOfType.size() == 1) {
            return beansOfType.entrySet().iterator().next().getValue();
        }

        return beansOfType.entrySet().stream()
                .filter(x -> x.getValue().getClass() == clazz)
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new RuntimeException("unable to find bean exactly for " + clazz));
    }

    public static <T> T inject(T bean) {
        appContext.getAutowireCapableBeanFactory().autowireBean(bean);
        return bean;
    }
}
