package com.github.bingoohuang.utils.lang;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.io.CharStreams;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.util.ClassUtils;
import org.springframework.util.SystemPropertyUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.annotation.Annotation;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ClzPath {
    public static String toStr(String resClasspath) {
        return toStr(resClasspath, Charsets.UTF_8);
    }

    public static List<String> toLines(String resClasspath) {
        return toLines(resClasspath, Charsets.UTF_8);
    }

    @SneakyThrows
    public static List<String> toLines(String resClasspath, Charset charset) {
        @Cleanup InputStream is = toInputStream(resClasspath);

        return CharStreams.readLines(new InputStreamReader(is, charset));
    }

    @SneakyThrows
    public static String toStr(String resClasspath, Charset charset) {
        @Cleanup InputStream is = toInputStream(resClasspath);
        return CharStreams.toString(new InputStreamReader(is, charset));
    }

    public static InputStream toInputStream(String resClasspath) {
        val res = ClzPath.class.getClassLoader().getResourceAsStream(resClasspath);
        if (res == null) throw new RuntimeException(resClasspath + " does not exist");

        return res;
    }

    public static Reader toReader(String resClasspath) {
        InputStream inputStream = toInputStream(resClasspath);
        return new InputStreamReader(inputStream, Charsets.UTF_8);
    }

    public static List<Class<?>> getClasses(String basePackage, Predicate<Class<?>> predicate) {
        return getClasses(basePackage, "**/*.class", predicate, null);
    }

    @SneakyThrows
    public static List<Class<?>> getClasses(String basePackage, String pattern, Predicate<Class<?>> classPredicate,
                                            Predicate<String> classNamePredicate) {
        val resolver = new PathMatchingResourcePatternResolver();
        val metaFactory = new CachingMetadataReaderFactory(resolver);

        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + "/" + pattern;
        Resource[] resources = resolver.getResources(packageSearchPath);

        ArrayList<Class<?>> clazzArr = new ArrayList<>();
        for (Resource res : resources) {
            if (!res.isReadable()) continue;

            val metadataReader = metaFactory.getMetadataReader(res);
            String className = metadataReader.getClassMetadata().getClassName();
            if (classNamePredicate != null && !classNamePredicate.apply(className)) continue;
            Class<?> clazz = Clz.findClass(className);
            if (clazz != null && classPredicate.apply(clazz)) clazzArr.add(clazz);
        }
        return clazzArr;
    }

    private static String resolveBasePackage(String basePackage) {
        return ClassUtils.convertClassNameToResourcePath(SystemPropertyUtils.resolvePlaceholders(basePackage));
    }

    public static List<Class<?>> getSubClasses(String basePackage, final Class<?> superClass) {
        return getSubClasses(basePackage, "**/*.class", superClass);
    }

    public static List<Class<?>> getSubClasses(String basePackage, String pattern, final Class<?> superClass) {
        return getClasses(basePackage, pattern, aClass -> false, null);
    }

    public static List<Class<?>> getAnnotatedClasses(String basePackage, final Class<? extends Annotation> annClass,
                                                     final Class<?>... excludesClasses) {
        return getAnnotatedClasses(basePackage, "**/*.class", annClass, excludesClasses);
    }

    public static List<Class<?>> getAnnotatedClasses(String basePackage, String pattern,
                                                     final Class<? extends Annotation> annClass, final Class<?>... excludesClasses) {
        return getClasses(basePackage, pattern, input -> input.isAnnotationPresent(annClass), Predicates.not(input -> {
            for (Class<?> class1 : excludesClasses) {
                if (Objects.equals(input, class1.getName())) return true;
            }
            return false;
        }));
    }

    public static Resource getResource(String classPathPattern) {
        return new ClassPathResource(classPathPattern);
    }

    @SneakyThrows
    public static Resource[] getResources(String basePackage, String classPathPattern) {
        val resolver = new PathMatchingResourcePatternResolver();

        String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                resolveBasePackage(basePackage) + "/" + classPathPattern;
        return resolver.getResources(packageSearchPath);
    }

}
