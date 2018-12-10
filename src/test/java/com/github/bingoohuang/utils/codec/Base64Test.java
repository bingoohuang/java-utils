package com.github.bingoohuang.utils.codec;


import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class Base64Test {
    @Test
    public void test() {
        String base64 = Base64.base64("黄进兵1", Base64.Format.Standard);
        assertThat(base64).isEqualTo("6buE6L+b5YW1MQ==");
        String base64Purified = Base64.base64("黄进兵1", Base64.Format.Purified);
        assertThat(base64Purified).isEqualTo("6buE6L+b5YW1MQ");

        String val = Base64.unBase64AsString(base64Purified);
        assertThat(val).isEqualTo("黄进兵1");
    }
}
