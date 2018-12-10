package com.github.bingoohuang.utils.math;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;


public class DoublesTest {

    @Test
    public void format() {
        assertThat(Doubles.format(1.00d)).isEqualTo("1");
        assertThat(Doubles.format(1.10d)).isEqualTo("1.1");
        assertThat(Doubles.format(0d)).isEqualTo("0");
        assertThat(Doubles.format(10d)).isEqualTo("10");
    }
}