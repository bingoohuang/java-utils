package com.github.bingoohuang.utils.conf;

import com.github.bingoohuang.utils.file.Pathx;
import com.github.bingoohuang.utils.lang.Classpath;
import com.github.bingoohuang.utils.lang.Str;
import com.google.common.primitives.Primitives;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.n3r.diamond.client.Miner;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Properties;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;

@Slf4j
public class ConfFactory {
    @SuppressWarnings("unchecked")
    public static <T> T create(Class<T> confInterface) {
        val cl = ConfFactory.class.getClassLoader();
        return (T) Proxy.newProxyInstance(cl, new Class[]{confInterface}, (proxy, method, args) -> {
            val props = createProperties(method);
            String value = parseValue(method, props);
            value = parseDefaultValue(method, value);

            return convertType(value, method);
        });
    }

    private static String parseDefaultValue(Method method, String value) {
        if (StringUtils.isNotEmpty(value)) return value;

        val defaultValue = method.getAnnotation(Conf.DefaultValue.class);
        if (defaultValue != null) return defaultValue.value();

        return value;
    }

    private static String parseValue(Method method, Properties props) {
        val confKey = method.getAnnotation(Conf.Key.class);
        if (confKey != null) return props.getProperty(confKey.value());

        val value = props.getProperty(method.getName());
        if (StringUtils.isNotEmpty(value)) return value;

        val name = LOWER_CAMEL.to(LOWER_UNDERSCORE, method.getName()).replace('_', '.');
        return props.getProperty(name);
    }

    @SneakyThrows
    private static Properties createProperties(Method method) {
        val source = method.getDeclaringClass().getAnnotation(Conf.Source.class);
        if (source == null) return dealDefaultProperties(method);

        val props = new Properties();
        for (val src : source.value()) {
            if (src.startsWith("diamond:")) {
                val axis = src.substring("diamond:".length());
                val group = StringUtils.substringBefore(axis, "^");
                val dataId = StringUtils.substringAfter(axis, "^");
                props.putAll(new Miner().getProperties(group, dataId));
            } else if (src.startsWith("classpath:")) {
                val cp = src.substring("classpath:".length());
                props.putAll(Classpath.loadProperties(cp));
            } else if (src.startsWith("file:")) {
                val file = src.substring("file:".length());
                val expand = Pathx.expandUserHome(file);
                val p = new Properties();
                p.load(new FileInputStream(expand));
                props.putAll(p);
            }
        }
        return props;
    }

    /**
     * 按照以下顺序，加载文件：
     * 1. classpath:conf/{SimpleClassName}.properties
     * 2. ~/.appconf/{SimpleClassName}.properties
     * 3. current_directory/conf/{SimpleClassName}.properties。
     * 4. Diamond client
     *
     * @param method 方法
     * @return 属性文件
     */
    private static Properties dealDefaultProperties(Method method) {
        val simpleName = method.getDeclaringClass().getSimpleName();
        val pfile = simpleName + ".properties";
        val p = Classpath.loadProperties("conf/" + pfile, true);

        val userHomeFile = new File(Pathx.userHome + "/.appconf/" + pfile);
        loadPropertiesFile(p, userHomeFile);

        val usdrDirFile = new File(Pathx.userDir + "/conf/" + pfile);
        loadPropertiesFile(p, usdrDirFile);

        p.putAll(new Miner().getProperties(simpleName, "default"));

        return p;
    }

    @SneakyThrows
    private static void loadPropertiesFile(Properties p, File file) {
        if (!file.exists()) return;

        val p1 = new Properties();
        p1.load(new FileInputStream(file));
        p.putAll(p1);
    }

    private static Object convertType(String value, Method method) {
        val rt = Primitives.unwrap(method.getReturnType());
        if (rt == String.class) return value;

        val converted = rt.isPrimitive() ? parsePrimitive(rt, value) : parseOther(rt, value);
        log.debug("get config {}.{} = {}", method.getDeclaringClass().getSimpleName(), method.getName(), converted);

        return converted;
    }

    private static Object parseOther(Class<?> rt, String value) {
        return null;
    }

    private static Object parsePrimitive(Class<?> rt, String value) {
        if (rt == boolean.class) return Str.anyOfIgnoreCase(value, "yes", "true", "on");
        if (rt == short.class) return Short.parseShort(value);
        if (rt == int.class) return Integer.parseInt(value);
        if (rt == long.class) return Long.parseLong(value);
        if (rt == float.class) return Float.parseFloat(value);
        if (rt == double.class) return Double.parseDouble(value);
        if (rt == byte.class) return Byte.parseByte(value);
        return null;
    }
}
