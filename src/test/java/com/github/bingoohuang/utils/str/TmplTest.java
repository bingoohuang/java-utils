package com.github.bingoohuang.utils.str;

import com.github.bingoohuang.utils.lang.Mapp;
import lombok.Builder;
import lombok.Data;
import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;


public class TmplTest {
    @Data @Builder
    public static class Abc {
        private String abc;
        private String e;
    }

    @Test
    public void evalBean() {
        assertThat(Tmpl.eval("#abc#",
                Abc.builder().abc("123").build())).isEqualTo("123");
        assertThat(Tmpl.eval("x#abc#y",
                Abc.builder().abc("123").build())).isEqualTo("x123y");
        assertThat(Tmpl.eval("x#abc#y, #e#",
                Abc.builder().abc("123").e("gg").build())).isEqualTo("x123y, gg");
    }

    @Test
    public void evalMap() {
        assertThat(Tmpl.eval("#abc#",
                Mapp.of("abc", "123"))).isEqualTo("123");
        assertThat(Tmpl.eval("x#abc#y",
                Mapp.of("abc", "123"))).isEqualTo("x123y");
        assertThat(Tmpl.eval("x#abc#y, #e#",
                Mapp.of("abc", "123", "e", "gg"))).isEqualTo("x123y, gg");
    }
}