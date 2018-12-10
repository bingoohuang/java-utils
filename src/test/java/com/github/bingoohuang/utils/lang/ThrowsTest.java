package com.github.bingoohuang.utils.lang;

import org.junit.Test;

import java.io.IOException;

public class ThrowsTest {

    @Test(expected = IOException.class)
    public void sneakyThrow() {
        Throws.sneakyThrow(new IOException());
    }
}