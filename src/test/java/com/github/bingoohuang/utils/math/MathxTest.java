package com.github.bingoohuang.utils.math;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class MathxTest {

    @Test
    public void roundHalfUp() {
        assertThat(Mathx.roundHalfUp("1", 1)).isEqualTo("1.0");
        assertThat(Mathx.roundHalfUp("1.11", 1)).isEqualTo("1.1");
        assertThat(Mathx.roundHalfUp("1.15", 1)).isEqualTo("1.2");
    }
}