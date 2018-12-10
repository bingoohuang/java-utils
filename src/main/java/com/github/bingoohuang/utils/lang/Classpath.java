package com.github.bingoohuang.utils.lang;

import com.github.bingoohuang.utils.codec.Bytes;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Classpath {

    public static boolean exists(String classpathPropertiesFileName) {
        return loadRes(classpathPropertiesFileName) != null;
    }


    @SneakyThrows
    public static Properties loadProperties(String classpathPropertiesFileName, boolean silent) {
        @Cleanup val is = loadRes(classpathPropertiesFileName);
        val p = new Properties();

        if (is != null) p.load(is);
        else if (!silent) {
            throw new RuntimeException("unable find classpath resource " + classpathPropertiesFileName);
        }

        return p;
    }

    @SneakyThrows
    public static Properties loadProperties(String classpathPropertiesFileName) {
        @Cleanup val is = loadRes(classpathPropertiesFileName);
        val p = new Properties();
        p.load(is);
        return p;
    }

    @SneakyThrows
    public static String loadResAsString(String classpath) {
        @Cleanup val is = loadRes(classpath);
        return Bytes.string(is);
    }

    /**
     * 从类路径加载资源文件。
     *
     * @param classpath 类路径
     * @return 输入流
     */
    public static InputStream loadRes(String classpath) {
        return Classpath.class.getClassLoader().getResourceAsStream(classpath);
    }

    public static File loadFile(String classpath) {
        val filePath = Classpath.class.getResource("/").getPath() + classpath;
        return new File(filePath);
    }


    @SneakyThrows
    public static List<String> loadResources(String name, ClassLoader classLoader) {
        ArrayList<String> list = new ArrayList();
        val loader = classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;
        val systemResources = loader.getResources(name);
        while (systemResources.hasMoreElements()) {
            @Cleanup val stream = systemResources.nextElement().openStream();
            val result = CharStreams.toString(new InputStreamReader(stream, Charsets.UTF_8));
            list.add(result);
        }

        return list;
    }
}
