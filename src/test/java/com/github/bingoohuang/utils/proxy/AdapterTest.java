package com.github.bingoohuang.utils.proxy;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.fail;

public class AdapterTest {

    public interface SubInterface {
        void doSome();
    }

    public static class Sub {

    }

    @Test
    public void adapt() {
        Adapter taskUtil = new Adapter();
        assertThat(Adapter.adapt(taskUtil, Adapter.class)).isSameAs(taskUtil);

        SubInterface adapt = Adapter.adapt(taskUtil, SubInterface.class);
        assertThat(adapt).isInstanceOf(SubInterface.class);
        assertThat(Adapter.adapt(taskUtil, Sub.class)).isInstanceOf(Sub.class);

        try {
            adapt.doSome();
            fail();
        } catch (Exception ex) {
            assertThat(ex.getCause()).isInstanceOf(NoSuchMethodException.class);
        }
    }
}