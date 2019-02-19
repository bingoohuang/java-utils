package com.github.bingoohuang.utils.proxy;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class CglibsTest {
    public interface SomeInterface {
        String doSomething();
    }

    @Data
    public static class MyBean implements SomeInterface {
        private String user;
        private String pass;

        @Override
        public String doSomething() {
            return "doSomething";
        }
    }

    public interface OtherInterface {
        String doOthers();
    }


    @Test @SneakyThrows
    public void test1() {
        val myBean = new MyBean();
        // 将一个实例，附加上一个接口的实现。
        OtherInterface proxied = Cglibs.mixin(myBean, (OtherInterface) myBean::doSomething);
        proxied.getClass().getMethod("getUser");
        assertThat(proxied.doOthers()).isEqualTo("doSomething");
    }
}
