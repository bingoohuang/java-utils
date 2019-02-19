package com.github.bingoohuang.utils.proxy;

import com.github.bingoohuang.utils.lang.Clz;
import lombok.experimental.UtilityClass;
import lombok.val;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

/**
 * @author bingoohuang [bingoohuang@gmail.com] Created on 2016/12/22.
 */
@UtilityClass
public class Cglibs {
    /**
     * proxied object class will be like
     * com.github.bingoohuang.westcache.RefreshTest$FlushBean$$EnhancerByCGLIB$$8b0b1c89
     * com.github.bingoohuang.westcache.springdemo.CacheInterfaceDemo$$EnhancerBySpringCGLIB$$5669a142
     *
     * @param obj cglib enhanced object.
     * @return underlined super class's name
     */
    public String getSuperClassName(Object obj) {
        String name = obj.getClass().getName();
        int posCglib = name.indexOf("$$EnhancerByCGLIB$$");
        if (posCglib > 0) return name.substring(0, posCglib);

        int posSpring = name.indexOf("$$EnhancerBySpringCGLIB$$");
        if (posSpring > 0) return name.substring(0, posSpring);

        return name;
    }

    /**
     * create a proxied object by cglib.
     *
     * @param superClass  target class.
     * @param interceptor interceptor.
     * @param interfaces  interfaces.
     * @return proxied object.
     */
    public Object proxy(Class<?> superClass,
                        MethodInterceptor interceptor,
                        Class<?>... interfaces) {
        return Enhancer.create(superClass, interfaces, interceptor);
    }

    // proxy class like redis.clients.jedis.Jedis$$EnhancerByCGLIB$$e3d540fd/ com.sun.proxy.$Proxy4
    public boolean isProxyClass(Class<?> targetClass) {
        val targetClassName = targetClass.getName();

        if (targetClassName.contains("CGLIB$$")) return true;
        return Proxy.isProxyClass(targetClass);

    }

    @SuppressWarnings("unchecked")
    public static <T> T proxy(Object bean, Object... otherImpls) {
        Set<Class> all = new HashSet<>();
        all.addAll(Clz.getAllInterfaces(bean.getClass()));
        for (val otherImpl : otherImpls) {
            all.addAll(Clz.getAllInterfaces(otherImpl.getClass()));
        }

        return (T) Enhancer.create(bean.getClass(), all.toArray(new Class[0]),
                (MethodInterceptor) (o, method, args, methodProxy) -> {
                    val m = Clz.findMethod(method, bean);
                    if (m.isPresent()) return m.get().invoke(args);

                    return Clz.findMethod(method, otherImpls).get().invoke(args);
                });
    }
}
