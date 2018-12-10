package com.github.bingoohuang.utils.lang;

import java.io.Closeable;
import java.lang.reflect.Method;

public class Closer {

    private Closer() {
    }

    /**
     * 关闭对象, 屏蔽所有异常。
     * 调用对象的close方法（如果对象有该方法的话）。
     *
     * @param objs 对象列表
     */
    public static void closeQuietly(Object... objs) {
        for (Object obj : objs) {
            closeQuietly(obj);
        }
    }

    /**
     * 关闭对象, 屏蔽所有异常。
     *
     * @param obj 待关闭对象
     */
    public static void closeQuietly(Object obj) {
        if (obj == null) return;

        if (obj instanceof Closeable) {
            try {
                ((Closeable) obj).close();
            } catch (Throwable e) {
                // Quietly
            }
            return;
        }

        Method method = Clz.getMethod(obj.getClass(), "close");
        if (method != null && method.getParameterTypes().length == 0) {
            Clz.invokeQuietly(obj, method);
        }
    }

}
