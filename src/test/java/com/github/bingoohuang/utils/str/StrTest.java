package com.github.bingoohuang.utils.str;

import com.github.bingoohuang.utils.lang.Str;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;


public class StrTest {
    @Test
    public void decode() {
        assertThat(Str.decode("男", "男", "先生", "女士")).isEqualTo("先生");
        assertThat(Str.decode("x", "男", "先生", "女士")).isEqualTo("女士");
    }

    @Test
    public void mask() {
        assertThat(Str.mask(null)).isEqualTo("*");
        assertThat(Str.mask("")).isEqualTo("*");
        assertThat(Str.mask("1")).isEqualTo("*");
        assertThat(Str.mask("12")).isEqualTo("1*");
        assertThat(Str.mask("123")).isEqualTo("1*3");
        assertThat(Str.mask("1234")).isEqualTo("1**4");
        assertThat(Str.mask("12345")).isEqualTo("1***5");
        assertThat(Str.mask("123456")).isEqualTo("12**56");
        assertThat(Str.mask("1234567")).isEqualTo("12***67");
        assertThat(Str.mask("12345678")).isEqualTo("12****78");
        assertThat(Str.mask("123456789")).isEqualTo("12*****89");
        assertThat(Str.mask("1234567890")).isEqualTo("123****890");
    }
}