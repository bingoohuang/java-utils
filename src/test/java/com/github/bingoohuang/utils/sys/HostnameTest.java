package com.github.bingoohuang.utils.sys;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class HostnameTest {
    @Test
    public void test() {
        assertThat(Hostname.HOSTNAME).isNotEmpty();
        assertThat(Hostname.HOSTNAME).isNotEqualTo("Unknown");
    }
}
