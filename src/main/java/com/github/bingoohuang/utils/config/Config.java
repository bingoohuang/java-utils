package com.github.bingoohuang.utils.config;

import com.github.bingoohuang.utils.config.impl.*;
import com.github.bingoohuang.utils.config.utils.ParamsApplyUtils;
import com.github.bingoohuang.utils.lang.ClzPath;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import java.util.List;
import java.util.Properties;

public class Config {

    private static Configable impl;

    static {
        loadConfigImplementation();
    }

    private static void loadConfigImplementation() {
        Configable defConfig = createConfigable("defconfigdir", "defconfig", null);
        Configable bizConfig = createConfigable("bizconfigdir", "bizconfig", defConfig);

        // 加载配置系统独立实现类（比如从Redis、Mysql、Oracle等读取配置的具体实现）
        // 要求具体配置类必须实现Configable接口，按照需要实现ParamsAppliable、DefConfigSetter接口
        // 例如：
        // config.implementation=org.n3r.config.impl.RedisConfigable(127.0.0.1,
        // 11211)
        String configImplementation = bizConfig.getStr("config.implementation");
        if (StringUtils.isEmpty(configImplementation)) {
            impl = bizConfig;
            return;
        }

        impl = loadImpl(configImplementation, bizConfig);
        if (impl instanceof DefConfigSetter) { // 设置缺省配置读取对象
            ((DefConfigSetter) impl).setDefConfig(defConfig);
        }
    }

    private static Configable loadImpl(String configImplementation, Configable defConfig) {
        return ParamsApplyUtils.createObject(configImplementation, Configable.class);
    }

    private static Configable createConfigable(String configKey, String defConfigDir, Configable defConfig) {
        ConfigBuilder configBuilder = new ConfigBuilder();
        configBuilder.setDefConfig(defConfig);

        String basePackage = defConfigDir;
        Resource envSpaceRes = ClzPath.getResource("envspace.props");
        if (envSpaceRes.exists()) {
            PropsConfigable envSpaceConfig = new PropsConfigable(envSpaceRes);
            basePackage = envSpaceConfig.getStr(configKey, defConfigDir);
            configBuilder.addConfig(envSpaceConfig);
        }

        Resource[] propertiesRes = ClzPath.getResources(basePackage, "**/*.properties");
        for (Resource propertieRes : propertiesRes) {
            configBuilder.addConfig(new PropertiesConfigable(propertieRes));
        }
        Resource[] propsRes = ClzPath.getResources(basePackage, "**/*.props");
        for (Resource propRes : propsRes) {
            configBuilder.addConfig(new PropsConfigable(propRes));
        }
        Resource[] iniRes = ClzPath.getResources(basePackage, "**/*.ini");
        for (Resource propRes : iniRes) {
            configBuilder.addConfig(new IniConfigable(propRes));
        }
        Resource[] tablesRes = ClzPath.getResources(basePackage, "**/*.table");
        for (Resource tableRes : tablesRes) {
            configBuilder.addConfig(new TableConfigable(tableRes));
        }

        return configBuilder.buildConfig();
    }

    public static boolean exists(String key) {
        return impl.exists(key);
    }

    public static Properties getProperties() {
        return impl.getProperties();
    }

    public static int getInt(String key) {
        return impl.getInt(key);
    }

    public static long getLong(String key) {
        return impl.getLong(key);
    }

    public static boolean getBool(String key) {
        return impl.getBool(key);
    }

    public static float getFloat(String key) {
        return impl.getFloat(key);
    }

    public static double getDouble(String key) {
        return impl.getDouble(key);
    }

    public static String getStr(String key) {
        return impl.getStr(key);
    }

    public static int getInt(String key, int defaultValue) {
        return impl.getInt(key, defaultValue);
    }

    public static long getLong(String key, long defaultValue) {
        return impl.getLong(key, defaultValue);
    }

    public static boolean getBool(String key, boolean defaultValue) {
        return impl.getBool(key, defaultValue);
    }

    public static float getFloat(String key, float defaultValue) {
        return impl.getFloat(key, defaultValue);
    }

    public static double getDouble(String key, double defaultValue) {
        return impl.getDouble(key, defaultValue);
    }

    public static String getStr(String key, String defaultValue) {
        return impl.getStr(key, defaultValue);
    }

    public static Configable subset(String prefix) {
        return impl.subset(prefix);
    }

    public static long refreshConfigSet(String prefix) {
        return impl.refreshConfigSet(prefix);
    }

    public static <T> T getBean(String key, Class<T> beanClass) {
        return impl.getBean(key, beanClass);
    }

    public static <T> List<T> getBeans(String key, Class<T> beanClass) {
        return impl.getBeans(key, beanClass);
    }

    /**
     * 提供一个可以获取配置impl的入口,
     * 用于展示出当前配置impl所有配置的结果集,
     * 修改了配置刷新相应配置,免重启服务,免发布.
     *
     * @return Configable
     */
    public static Configable getConfigImpl() {
        return impl;
    }

}
