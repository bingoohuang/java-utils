package com.github.bingoohuang.utils.str;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;

public class FmtTest {

    @Test
    public void format() {
        assertThat(Fmt.format("hello {}", "bingoo")).isEqualTo("hello bingoo");
    }
}